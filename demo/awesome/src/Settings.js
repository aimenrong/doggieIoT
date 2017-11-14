"use strict"


class Settings {
	constructor() {
		this.brokerHost = "localhost";
		this.brokerPort = 1884;
		this.cid = "terry-js";
	}

	saveSettings(host, port) {
		this.brokerHost = host;
		this.brokerPort = port;
	}

	getCid() {
		return this.cid;
	}

	getHost() {
		return this.brokerHost;
	}

	getPort() {
		return this.brokerPort;
	}
}
export default Settings