var sys = require('sys');
var exec = require('child_process').exec;
var express = require('express');
var bodyParser = require('body-parser');

var app = express();
var child;
var appName = "Spatial Database";

var allowCrossDomain = function(req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET,POST');
    res.header('Access-Control-Allow-Headers', 'Content-Type');
    next();
}

app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(bodyParser.json());
app.use(allowCrossDomain);

app.get('/apoteks', function (req, res) {
    console.log("/apoteks");

    child = exec("java -cp jar/postgis.jar:jar/postgresql-9.4-1201.jdbc4.jar:. spatialdatabase.SpatialDatabase getApoteks", function (error, stdout, stderr) {
        var temp = [];
        var data = stdout.split("\n");
//console.log(stdout);
        for (var i=0;i<data.length-1;i++){
            var name = data[i].split(",")[0];
            var sub = data[i].split(",")[1].split(" ");
            var lng = sub[0].substring(6);
            var lat = sub[1].substring(0,sub[1].length-1);
            //console.log("name: " + name + "lat: " + lat + "lng: " + lng);
            temp.push({name: name, lat: lat, lng: lng});
        } 

        res.send(JSON.stringify(temp));
    });
});

app.get('/viruses', function (req, res) {
    console.log("/viruses");
    child = exec("java -cp jar/postgis.jar:jar/postgresql-9.4-1201.jdbc4.jar:. spatialdatabase.SpatialDatabase getViruses", function (error, stdout, stderr) {
        var temp = [];
        var data = stdout.split("\n");

        for (var i=0;i<data.length-1;i++){
            var sub = data[i].split(" ");
            var lng = sub[0].substring(6);
            var lat = sub[1].substring(0,sub[1].length-1);
            //console.log("lat: " + lat + "lng: " + lng);
            temp.push({lat: lat, lng: lng});
        } 

        res.send(JSON.stringify(temp));
    });
});

app.post('/infected', function (req, res) {
    console.log("/infected");
    var body = req.body;
    
    var lat = body.lat;
    var lng = body.lng;
    console.log("Checking Infection at lat : " + parseFloat(lat) + " lng : " + parseFloat(lng)); 

    child = exec("java -cp jar/postgis.jar:jar/postgresql-9.4-1201.jdbc4.jar:. spatialdatabase.SpatialDatabase getInfected " + lat + " " + lng, function (error, stdout, stderr) {
        if (error !== null) {
            console.log('exec error: ' + error);
            es.send(JSON.stringify({success:"0"}));
            process.exit(1);
        }else{
            console.log(stdout);
            if (!stdout.equals("")){
                var temp = stdout.split(",");
                var name = temp[0];
                var lng = temp[1].split(" ")[0].substring(6);
                var lat = temp[1].split(" ")[1].substring(0, temp[1].split(" ")[1].length-1);
                var dist = temp[2];
                res.send(JSON.stringify({success:"1", data:{name:name, lat:lat, lng:lng, dist:dist}}));    
            }else{
                res.send(JSON.stringify({success:"1", data:{}}));    
            }
            
        }
    });
});

app.post('/add', function (req, res) {
    console.log("/addViruses");
    var body = req.body;
    console.log(body);

    for (i in body){
        var lat = body[i].lat;
        var lng = body[i].lng;
        console.log("Adding lat : " + parseFloat(lat) + " lng : " + parseFloat(lng));
    
        child = exec("java -cp jar/postgis.jar:jar/postgresql-9.4-1201.jdbc4.jar:. spatialdatabase.SpatialDatabase addViruses " + lng + " " + lat, function (error, stdout, stderr) {
            if (error !== null) {
                console.log('exec error: ' + error);
                res.send(JSON.stringify({success:"0"}));
                process.exit(1);
            }
        });
    }
    res.send(JSON.stringify({success:"1"}));
});

var server = app.listen(8080, function () {
    var port = server.address().port;

    child = exec("javac -cp jar/postgis.jar:jar/postgresql-9.4-1201.jdbc4.jar spatialdatabase/SpatialDatabase.java", function (error, stdout, stderr) {
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