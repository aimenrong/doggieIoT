"use strict";
// mqttService.js
import Settings from '../Settings'

var settings = new Settings();

class MqttService {
	constructor() {
		this.cid = "terry-js";
		this.client = null;
	}

	connect(onMessageArrivedCallback, onConnectionLostCallback) {
		console.log('MqttService.connect');
		if (this.client == null || this.client == undefined || this.client == '') {
	        this.client = new Paho.MQTT.Client(settings.getHost(), Number(settings.getPort()), this.cid );
	        this.client.onConnect = onConnect;
	        this.client.onMessageArrived = onMessageArrived;
	        this.client.onConnectionLost = onConnectionLost;
	        this.client.connect({onSuccess: onConnect, onFailure: onFailure, cleanSession:false});
	    } else {
	        alert('Already Connect');
	    }
	}

	sendNotification(longitude, latitude, content) {
		try {
	        if (client == null || client == undefined || client == '') {
	            alert('Not connect yet');
	            return;
	        }

	        var data = {'deviceId' : settings.getCid(), 'longitude' : longitude, 'latitude' : latitude, 'content' : content};
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
}

export default MqttService