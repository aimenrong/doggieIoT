<template>
<b-nav pills>
  <b-nav-item-dropdown id="nav7_ddown" text="Actions" right>
    <b-dropdown-item v-on:click="connect($event)">{{ actionName }}</b-dropdown-item>
  </b-nav-item-dropdown>
  <b-nav-item-dropdown id="nav7_ddown" text="Notifications" right>
    <b-dropdown-item>one</b-dropdown-item>
    <b-dropdown-item>two</b-dropdown-item>
    <b-dropdown-divider></b-dropdown-divider>
    <b-dropdown-item>three</b-dropdown-item>
  </b-nav-item-dropdown>
  <b-nav-item-dropdown id="nav7_ddown" text="Subscriptions" right>
    <b-dropdown-item>one</b-dropdown-item>
    <b-dropdown-item>two</b-dropdown-item>
    <b-dropdown-divider></b-dropdown-divider>
    <b-dropdown-item>three</b-dropdown-item>
  </b-nav-item-dropdown>
</b-nav>
</template>

<script>
import MqttService from '../service/MqttService'
var mqttService = new MqttService();

export default {
  name: 'DoggieNav',
  data() {
    return {
    actionName : 'Connect'
    }
  },
  methods : {
  connect : function (event) {
    var name = document.querySelector("#vue input");
    console.log('Connect');
    mqttService.connect(event);
    this.actionName = this.actionName == 'Disconnect' ? 'Connect' : 'Disconnect';
  }}
}
</script>

<style>

</style>
