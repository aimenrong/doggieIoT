"use strict"


class Settings {
	constructor() {
		this.brokerHost = "localhost";
		this.brokerPort = 1884;
		this.cid = "terry-js";
	}

	saveSettings(host, port, clientId) {
		this.brokerHost = host;
		this.brokerPort = port;
		this.cid = clientId;
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
export default new Settings()