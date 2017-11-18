<template>
<div>
<b-navbar toggleable="md" type="dark" variant="info">
  <b-nav-toggle target="nav_collapse"></b-nav-toggle>
  <b-navbar-brand href="#">Doggie</b-navbar-brand>

  <b-collapse is-nav id="nav_collapse">

    <b-nav is-nav-bar>
      <b-nav-item v-bind:class="{ connected : isConnected }" href="#">{{ connectionStatus }}</b-nav-item>
    </b-nav>

    <!-- Right aligned nav items -->
    <b-nav is-nav-bar class="ml-auto">

      <b-nav-form>
        <b-form-input size="sm" class="mr-sm-2" type="text" placeholder="Input address to search"/>
        <b-button size="sm" class="my-2 my-sm-0" type="submit">Search</b-button>
      </b-nav-form>

      <b-nav-item-dropdown text="Setting" right>
        <b-dropdown-item style="cursor: pointer" @click="openAccountSetting">Account</b-dropdown-item>
      </b-nav-item-dropdown>
    </b-nav>

  </b-collapse>
</b-navbar>
<b-modal v-model="settingsModalShow"
    title="Settings"
    :header-bg-variant="headerBgVariant"
    :header-text-variant="headerTextVariant"
    :body-bg-variant="bodyBgVariant"
    :body-text-variant="bodyTextVariant"
    :footer-bg-variant="footerBgVariant"
    :footer-text-variant="footerTextVariant">
    <b-container fluid>
      <b-row class="mb-1">
        <b-col cols="2">Server Host</b-col>
        <b-col><b-form-input v-model="txtServer"
          type="text"
          placeholder="Enter Server Host">{{ txtServer }}</b-form-input></b-col>
      </b-row>
      <b-row class="mb-1">
        <b-col cols="2">Server Port</b-col>
        <b-col><b-form-input v-model="txtPort"
          type="text"
          placeholder="Enter Server Port">{{ txtPort }}</b-form-input></b-col>
      </b-row>
      <b-row>
        <b-col cols="2">Client ID</b-col>
        <b-col><b-form-input v-model="txtClientId"
          type="text"
          placeholder="Enter Client ID">{{ txtClientId }}</b-form-input></b-col>
      </b-row>
    </b-container>
    <div slot="modal-footer" class="w-100">
      <b-btn size="sm" class="float-right m-1" variant="primary" @click="settingsModalShow=false">
        Close
      </b-btn>
      <b-btn size="sm" class="float-right m-1" variant="primary" @click="saveAccountSetting">
        Save
      </b-btn>
    </div>
  </b-modal>
</div>
</template>

<script>
import settings from '../Settings'
import eventBus from '../service/eventBus'

export default {
  name: 'DoggieNavBar',
  created() {
      console.log('call DoggieNavBar created');
      eventBus.$on('onConnect', this.onConnect);
      eventBus.$on('onFailure', this.onFailure);
      eventBus.$on('onConnectionLost', this.onConnectionLost);
     },
  methods : {
    openAccountSetting() {
      console.log("Call openAccountSetting");
      this.txtServer = settings.getHost();
      this.txtPort = settings.getPort();
      this.txtClientId = settings.getCid();
      this.settingsModalShow = true;
    },
    saveAccountSetting() {
      console.log("Call saveAccountSetting");
      settings.saveSettings(this.txtServer, this.txtPort, this.txtClientId);
      this.settingsModalShow = false;
    },
    onConnect() {
      console.log('call onConnect');
      this.isConnected = true;
      this.connectionStatus = 'Connected';
    },
    onFailure() {
      this.isConnected = false;
      this.connectionStatus = 'Failure';
    },
    onConnectionLost() {
      this.isConnected = false;
      this.connectionStatus = 'Disconnected';
    }
  },
  data() {
    return {
      isConnected : false,
      connectionStatus : 'Disconnected',
      txtServer : 'localhost',
      txtPort : 1884,
      txtClientId : 'terry-js',
      settingsModalShow : false,
      variants: [
              'primary','secondary','success','warning','danger','info','light','dark'
              ],
              headerBgVariant: 'dark',
              headerTextVariant: 'light',
              bodyBgVariant: 'light',
              bodyTextVariant: 'dark',
              footerBgVariant: 'warning',
              footerTextVariant: 'dark'
    };
  }
}
</script>

<style>
.connected {
  background-color: #8f8;
  color: blue;
}
</style>
