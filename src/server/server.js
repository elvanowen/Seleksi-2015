var sys = require('sys');
var exec = require('child_process').exec;
var express = require('express');
var bodyParser = require('body-parser');

var app = express();
var child;
var appName = "Spatial Database";

app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(bodyParser.json());

app.get('/apoteks', function (req, res) {
    console.log("/apoteks");

    child = exec("cd ..;java -cp jar/postgis.jar:jar/postgresql-9.4-1201.jdbc4.jar:. spatialdatabase.SpatialDatabase getApoteks", function (error, stdout, stderr) {
        var temp = [];
        var data = stdout.split("\n");

        for (var i=0;i<data.length-1;i++){
            var name = data[i].split(",")[0];
            var sub = data[i].split(",")[1].split(" ");
            var lat = sub[0].substring(7);
            var lng = sub[1].substring(0,sub[1].length-1);
            console.log("name: " + name + "lat: " + lat + "lng: " + lng);
            temp.push({name: name, lat: lat, lng: lng});
        } 

        res.send(JSON.stringify(temp));
    });
});

app.get('/viruses', function (req, res) {
    console.log("/viruses");
    child = exec("cd ..;java -cp jar/postgis.jar:jar/postgresql-9.4-1201.jdbc4.jar:. spatialdatabase.SpatialDatabase getViruses", function (error, stdout, stderr) {
        var temp = [];
        var data = stdout.split("\n");

        for (var i=0;i<data.length-1;i++){
            var sub = data[i].split(" ");
            var lat = sub[0].substring(7);
            var lng = sub[1].substring(0,sub[1].length-1);
            console.log("lat: " + lat + "lng: " + lng);
            temp.push({lat: lat, lng: lng});
        } 

        res.send(JSON.stringify(temp));
    });
});

app.get('/infected', function (req, res) {
    console.log("/infected");
    var body = req.body;
    
    var lat = body.lat;
    var lng = body.lng;
    console.log(lat);
    console.log(lng);  

    child = exec("cd ..;java -cp jar/postgis.jar:jar/postgresql-9.4-1201.jdbc4.jar:. spatialdatabase.SpatialDatabase getInfected " + lat + " " + lng, function (error, stdout, stderr) {
        if (error !== null) {
            console.log('exec error: ' + error);
        }else{
            var temp = {};
            var data = stdout.split("\n");

            if (data[0].equals("true")){
                var sub = data[1].split(" ");
                var lat = sub[i][0].substring(7);
                var lng = sub[i][1].substring(0,sub[i][1].length-1);
                console.log("lat: " + lat + "lng: " + lng);
                temp = {lat: lat, lng: lng};
            }

            res.send(JSON.stringify(temp));
        }
    });
});

app.post('/add', function (req, res) {
    console.log("/addViruses");
    var body = req.body;
    
    for (i in body){
        var lat = body[i].lat;
        var lng = body[i].lng;
        console.log(lat);
        console.log(lng);   
    

        child = exec("cd ..;java -cp jar/postgis.jar:jar/postgresql-9.4-1201.jdbc4.jar:. spatialdatabase.SpatialDatabase addViruses " + lat + " " + lng, function (error, stdout, stderr) {
            if (error !== null) {
                console.log('exec error: ' + error);
            }else{
                res.send(stdout);
            }
        });
    }
});

var server = app.listen(8080, function () {
    var port = server.address().port;

    child = exec("cd ..;javac -cp jar/postgis.jar:jar/postgresql-9.4-1201.jdbc4.jar spatialdatabase/SpatialDatabase.java", function (error, stdout, stderr) {
        if (error !== null || stdout !== "") {
            console.log('exec error: ' + error);
            console.log(stdout);
            process.exit(1);
        }else{
            console.log("Compiled succesful");
            console.log(appName + ' listening at %s', port);
        }
    });
});