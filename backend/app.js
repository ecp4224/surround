var express = require('express');
var bodyParser = require('body-parser');
var songs = require('./datastore.js');
var app = express();
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
    extended: true
}));

app.listen(8080);
app.get('/api/fetch', function(req, res) {
    if (!req.query.lat || !req.query.long) {
        res.status(500);
        res.send("Invalid request!");
        return;
    }

    var lat = req.query.lat;
    var long = req.query.long;

    songs.getSongs(lat, long, function(found) {
        res.send(JSON.stringify(
            found
        ));
    }, function(e) {
        res.status(500);
        res.send(e);
    });
});

app.post('/api/post', function(req, res) {
    if (!req.body.lat || !req.body.long || !req.body.songName || !req.body.artist) {
        res.status(500);
        res.send("Invalid request!");
        return;
    }

    var lat = parseFloat(req.body.lat);
    var long = parseFloat(req.body.long);
    var songName = req.body.songName;
    var artist = req.body.artist;
    songs.pushSong(lat, long, songName, artist, function(s) {
        res.send(JSON.stringify(
            s
        ));
    }, function(e) {
        res.status(500);
        res.send(e);
    })
});

app.get('/api/social/friends', function(req, res) {
    if (!req.query.lat || !req.query.long || !req.query.fb_access_token) {
        res.status(500);
        res.send("Invalid request!");
        return;
    }

    var lat = req.query.lat;
    var long = req.query.long;
    var fb_access_token = req.query.fb_access_token;

    songs.getFriends(lat, long, fb_access_token, function(a) {
        res.send(JSON.stringify(
            a
        ));
    }, function(e) {
        res.status(500);
        res.send(e);
    })
});
