'use strict';
// mqttService.js
import settings from '../Settings'
import eventBus from './eventBus'

var client;
var cid = settings.getCid();

class MqttService {
	constructor() {
		
	}

	connect() {
		console.log('MqttService.connect');
		if (client == null || client == undefined || client == '') {
	        client = new Paho.MQTT.Client(settings.getHost(), Number(settings.getPort()), cid);
	        client.onConnect = this.onConnect;
	        client.onMessageArrived = this.onMessageArrived;
	        client.onConnectionLost = this.onConnectionLost;
	        client.connect({onSuccess: this.onConnect, onFailure: this.onFailure, cleanSession : false});
	    } else {
	        alert('Already Connect');
	    }
	}

	disconnect() {
	    client.disconnect();
	    client = null;
	}

	onConnect() {
		console.log("Callback onConnect");
		client.subscribe("device-notification", {qos:1});
		eventBus.$emit('onConnect');
		eventBus.$emit('onConnectAction');
	}

	onMessageArrived(message) {
		eventBus.$emit('onMessageArrivedAction', message.payloadString);
	}

	onFailure() {
		eventBus.$emit('onFailure');
		eventBus.$emit('onFailureAction');
	}

	onConnectionLost() {
		eventBus.$emit('onConnectionLost');
		eventBus.$emit('onConnectionLostAction');
	}

	sendNotification(longitude, latitude, content) {
		try {
	        if (client == null || client == undefined || client == '') {
	            alert('Not connect yet');
	            return;
	        }

	        var data = {'deviceId' : settings.getCid(), 'longitude' : longitude, 'latitude' : latitude, 'content' : content};
	        var msg = JSON.stringify(data);
	        var message = new Paho.MQTT.Message(msg);
	        message.destinationName = "device-event";
	        message.qos = 1;
	        client.send(message);
	    } catch(e) {
	        console.error(e)
	    }
	}
}

export default MqttService