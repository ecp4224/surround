var radius = 0.00076346643;
var MongoClient = require('mongodb').MongoClient;

var url = 'mongodb://localhost:27017/trails';
var database;
var songCollection;
var userCollection;

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


module.exports = {

    /*
    Return an array of song objects.
    When the function complete, invoke the completedCallback function with the array as a parameter, if an error
    occurred, then invoke the errorCallback with the error as a parameter
     */
    getSongs: function(lat, long, completedCallback, errorCallback) {
        validateDatabase(function() {
            var lowerLat = lat - radius;
            var highLat = lat + radius;
            var lowerLong = long - radius;
            var highLong = long + radius;

            songCollection.find({
                'lat': {$gte: lowerLat, $lte: highLat},
                'long': {$gte: lowerLong, $lte: highLong}
            }).toArray(function(err, docs) {
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
    pushSong: function(lat, long, name, artist, completedCallback, errorCallback) {
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
    }
};