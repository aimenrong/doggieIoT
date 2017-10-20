
var cid = 'terry-js2'
var client;


// Web Messaging API callbacks
var onSuccess = function(value) {
    $('#status').toggleClass('disconnected', false);
    $('#status').toggleClass('failConnect', false);
    $('#status').toggleClass('connected', true);
    $('#status').text('Success');
}

var onConnect = function(frame) {
    $('#status').text('Connected');
    $('#status').toggleClass('disconnected', false);
    $('#status').toggleClass('failConnect', false);
    $('#status').toggleClass('connected', true);
    client.subscribe("device-notification", {qos:1});
}
var onFailure = function(error) {
    $('#status').toggleClass('failConnect',true);
    $('#status').text("Failure");
    $('#menuConnect').text('Connect');
    client = null;
}

function onConnectionLost(responseObject) {
    $('#status').text('Disconnected');
    $('#status').toggleClass('connected', false);
    $('#status').toggleClass('disconnected', true);
}

function onMessageArrived(message) {
    $('#notificationList').prepend('<li>' + message.payloadString + '</li>');
    var modalShow = ($("#notificationModal").data('bs.modal') || {_isShown: false})._isShown;
    if (!modalShow) {
        $('#notificationModal').modal();
    }
}
function doSubscribe() {

}
function doDisconnect() {
    client.disconnect();
    client = null;
}

function connectToHost(host, port) {
    if (client == null || client == undefined || client == '') { 
        client = new Paho.MQTT.Client(host, Number(port), cid );
        client.onConnect = onConnect;
        client.onMessageArrived = onMessageArrived;
        client.onConnectionLost = onConnectionLost;
        client.connect({onSuccess: onConnect, onFailure: onFailure, cleanSession:false});
    } else {
        alert('Already Connect');
    }

}

function submitNotification(longitude, latitude, content) {
    try {
        if (client == null || client == undefined || client == '') { 
            alert('Not connect yet');
            return;
        }

        var data = {'deviceId' : cid, 'longitude' : longitude, 'latitude' : latitude, 'content' : content};
        var msg = JSON.stringify(data);
        message = new Paho.MQTT.Message(msg);
        message.destinationName = "device-event";
        message.qos = 1;
        client.send(message);
        $('#subscriptionList').prepend('<li>' + msg + '</li>');
        $('#subscriptionListModal').modal();
    } catch(e) {
        console.error(e)
    }
}