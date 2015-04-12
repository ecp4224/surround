var radius = 0.00076346643;
var MongoClient = require('mongodb').MongoClient;
var FB = require('fb');

var url = 'mongodb://localhost:27017/trails';
var database;
var songCollection;
var userCollection;

FB.options({
    appId: '809303289155048',
    appSecret: 'b7efc79b8d7da524550403182fc1aa40'
});

var connectToDatabase = function (completed) {
    MongoClient.connect(url, function (err, db) {
        if (err)
            throw err;

        console.log("Connected to mongodb!");

        database = db;

        songCollection = db.collection('songs');
        userCollection = db.collection('users');

        completed();
    });
};

var validateDatabase = function(completed) {
    if (!database) {
        connectToDatabase(completed);
    }
    else if (!database.serverConfig.isConnected())
        connectToDatabase(completed);
    else
        completed();
};

var updateUser = function(fb_id, lat, long) {
    userCollection.update(
        { 'fb_id': fb_id },
        {
            'latitude': lat,
            'longitude': long,
            lastActive: Math.floor(new Date() / 1000)
        }
    );
};

var validateAndCreateUser = function(facebookId, accessToken, lat, long, completedCallback, errCallback) {
    validateDatabase(function() {
        userCollection.find({
            'fb_id': facebookId
        }).toArray(function(err, docs) {
            if (err) {
                errCallback(err);
                return;
            }

            if (docs.length > 0){
                completedCallback(docs[0]);
                return;
            }

            userCollection.count({}, function(err, numOfDocs) {
                FB.api('/me', {
                    access_token: accessToken
                }, function(result) {
                    if (!result || result.error) {
                        err("Failed to create user!");
                        return;
                    }
                    var meObject = JSON.parse(result);

                    var user = {
                        id: numOfDocs,
                        name: meObject.name,
                        fb_id: meObject.id,
                        lastActive: Math.floor(new Date() / 1000),
                        'latitude': lat,
                        'longitude': long
                    };

                    songCollection.insert(user, function (err, result) {
                        if (err) {
                           errCallback(err);
                            return;
                        }

                        completedCallback(user);
                    });
                });
            });
        })
    });
};


module.exports = {

    /*
    Return an array of song objects.
    When the function complete, invoke the completedCallback function with the array as a parameter, if an error
    occurred, then invoke the errorCallback with the error as a parameter
     */
    getSongs: function(lat, long, completedCallback, errorCallback, fb_id) {
        validateDatabase(function() {
            var lowerLat = lat - radius;
            var highLat = parseFloat(lat) + radius;
            var lowerLong = long - radius;
            var highLong = parseFloat(long) + radius;

            songCollection.find({
                'latitude': {$gte: '' + lowerLat, $lte: '' + highLat},
                'longitude': {$lte: '' + lowerLong, $gte: '' + highLong}
            }).toArray(function(err, docs) {
                if (fb_id) {
                    updateUser(fb_id, lat, long);
                }
                if (err)
                    errorCallback(err);
                else
                    completedCallback(docs);
            });
        });
    },

    /*
     Return the song object saved/found
     When the function complete, invoke the completedCallback function with the array as a parameter, if an error
     occurred, then invoke the errorCallback with the error as a parameter
     */
    pushSong: function(lat, long, name, artist, completedCallback, errorCallback, fb_id) {
        validateDatabase(function() {

            var lowerLat = lat - radius;
            var highLat = lat + radius;
            var lowerLong = long - radius;
            var highLong = long + radius;

            songCollection.find({
                'name': name,
                'artist': artist,
                'lat': {$gte: lowerLat, $lte: highLat},
                'long': {$gte: lowerLong, $lte: highLong}
            }).toArray(function (err, docs) {
                if (err) {
                    errorCallback(err);
                    return;
                }

                if (docs.length > 0) {
                    completedCallback(docs[0]);
                } else {
                    songCollection.count({}, function (err, numOfDocs) {
                        if (err) {
                            errorCallback(err);
                            return;
                        }

                        var song = {
                            'id': numOfDocs,
                            'latitude': lat,
                            'longitude': long,
                            'name': name,
                            'artist': artist,
                            'user_id': -1,
                            'timePosted': Math.floor(new Date() / 1000),
                            'genre': []
                        };

                        songCollection.insert(song, function (err, result) {
                            if (fb_id) {
                                updateUser(fb_id, lat, long);
                            }

                            if (err) {
                                errorCallback(err);
                                return;
                            }

                            completedCallback(song);
                        });
                    });
                }
            });
        });
    },

    getFriends: function(lat, long, fb_access_token, completedCallback, errCallback) {
        validateDatabase(function() {
            FB.api('me/friends', {
                access_token: fb_access_token
            }, function(result) {
                if (!result || result.error) {
                    errCallback("Failed to get friends!");
                    return;
                }

                var friendArray = JSON.parse(result).data;

                var lowerLat = lat - radius;
                var highLat = parseFloat(lat) + radius;
                var lowerLong = long - radius;
                var highLong = parseFloat(long) + radius;

                userCollection.find({
                    'latitude': {$gte: '' + lowerLat, $lte: '' + highLat},
                    'longitude': {$lte: '' + lowerLong, $gte: '' + highLong},
                    'fb_id': { $in: friendArray }
                }).toArray(function(err, docs) {
                    if (err)
                        errCallback(err);
                    else
                        completedCallback(docs);
                });
            })
        });
    }
};