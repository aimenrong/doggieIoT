<template>
	<div>
		<div id="mapContainer" style="width:100%; height:800px"></div>
		<b-modal v-model="subscriptionModalShow"
		title="Subscriptions"
		:header-bg-variant="headerBgVariant"
		:header-text-variant="headerTextVariant"
		:body-bg-variant="bodyBgVariant"
		:body-text-variant="bodyTextVariant"
		:footer-bg-variant="footerBgVariant"
		:footer-text-variant="footerTextVariant">
		<b-container fluid>
			<b-row class="mb-1">
				<b-col cols="2">Longitude</b-col>
				<b-col><b-form-input v-model="txtLongitude"
					type="text"
					placeholder="Enter Longitude">{{ txtLongitude }}</b-form-input></b-col>
				</b-row>
				<b-row class="mb-1">
					<b-col cols="2">Latitude</b-col>
					<b-col><b-form-input v-model="txtLatitude"
						type="text"
						placeholder="Enter Latitude">{{txtLatitude}}</b-form-input></b-col>
					</b-row>
					<b-row>
						<b-col cols="2">Address</b-col>
						<b-col><b-form-input v-model="txtAddress"
							type="text"
							placeholder="Enter Address">{{txtAddress}}</b-form-input></b-col>
						</b-row>
						<b-row>
							<b-col cols="2">Content</b-col>
							<b-col><b-form-input v-model="txtNotificationCnt"
								type="text"
								placeholder="Enter Content">{{txtNotificationCnt}}</b-form-input></b-col>
							</b-row>
						</b-container>
						<div slot="modal-footer" class="w-100">
							<b-btn size="sm" class="float-right m-1" variant="primary" @click="subscriptionModalShow=false">
								Close
							</b-btn>
							<b-btn size="sm" class="float-right m-1" variant="primary" @click="sendSubscription">
								Save
							</b-btn>
						</div>
					</b-modal>
				</div>
			</template>

			<script>
				import AMap from 'AMap'
				import MqttService from '../service/MqttService'
				var map;
				var geocoder;
				var mqttService = new MqttService();

				export default {
					name: 'DoggieContent',
					mounted: function () {
						this.init()
					},
					data () {
						return {
							subscriptionModalShow: false,
							txtLongitude : 0,
							txtLatitude : 0,
							txtAddress : '',
							txtNotificationCnt : '',
							variants: [
							'primary','secondary','success','warning','danger','info','light','dark'
							],
							headerBgVariant: 'dark',
							headerTextVariant: 'light',
							bodyBgVariant: 'light',
							bodyTextVariant: 'dark',
							footerBgVariant: 'warning',
							footerTextVariant: 'dark'
						}
					},
					methods: {
						init () {
							var self = this;
							console.log("call init function");
							map = new AMap.Map('mapContainer', {
								resizeEnable: true,
								zoom: 10,
								center: [113.257978, 23.127072]
							});

							AMap.service('AMap.Geocoder',function(){
								geocoder = new AMap.Geocoder({
									city: "010"
								});
							});
							var clickEventListener = map.on('click', function(e) {
								var longitude = e.lnglat.getLng();
								var latitude = e.lnglat.getLat();
								console.log("longitude : " + longitude);
								console.log("latitude : " + latitude);
								self.txtLongitude = longitude;
								self.txtLatitude = latitude;
								var lnglatXY=[longitude, latitude];
								geocoder.getAddress(lnglatXY, function(status, result) {
									if (status === 'complete' && result.info === 'OK') {
										console.log(result.regeocode.formattedAddress);
										self.txtAddress = result.regeocode.formattedAddress;
										self.subscriptionModalShow = true;
									} else{
										alert(result);
									}
								}); 
							});
							var auto = new AMap.Autocomplete({
								input: "tipinput"
							});
							AMap.event.addListener(auto, "select", function (e) {
								if (e.poi && e.poi.location) {
									map.setZoom(15);
									map.setCenter(e.poi.location);
								}
							});


							map.plugin('AMap.Geolocation', function() {
								geolocation = new AMap.Geolocation({
									enableHighAccuracy: true,
									timeout: 10000,          
									buttonOffset: new AMap.Pixel(10, 20),
									zoomToAccuracy: true,     
									buttonPosition:'RB'
								});
								map.addControl(geolocation);
								geolocation.getCurrentPosition();
								AMap.event.addListener(geolocation, 'complete', onComplete);
								AMap.event.addListener(geolocation, 'error', onError);     
							});
						},
						showModal() {
							this.$refs.myModalRef.show();
						},
						hideModal() {
							this.$refs.myModalRef.hide();
						},
						sendSubscription() {
							console.log('Call sendSubscription');
							this.subscriptionModalShow = false;
						}
					}
				}
			</script>

			<style>

			</style>
