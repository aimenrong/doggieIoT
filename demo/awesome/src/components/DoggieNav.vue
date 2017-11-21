<template>
<div>
<b-nav pills>
  <b-nav-item-dropdown id="nav7_ddown" text="Actions" right>
    <b-dropdown-item v-on:click="connect">{{ actionName }}</b-dropdown-item>
  </b-nav-item-dropdown>
  <b-nav-item-dropdown id="nav7_notifications" :text="notificationMenuText" right>

    <b-dropdown-item v-on:click="viewNotification">View Notifications
    </b-dropdown-item>
  </b-nav-item-dropdown>
  <b-nav-item-dropdown id="nav7_ddown" text="Subscriptions" right>
    <b-dropdown-item v-on:click="viewSubscriptions">View Subscriptions</b-dropdown-item>
  </b-nav-item-dropdown>
  <b-nav-item-dropdown id="nav7_ddown" text="Tools" right>
    <b-dropdown-item v-on:click="retrieveMyGps">Retrieve My GPS</b-dropdown-item>
  </b-nav-item-dropdown>
</b-nav>

<b-modal v-model="notificationModalShow"
    title="Notifications"
    :header-bg-variant="headerBgVariant"
    :header-text-variant="headerTextVariant"
    :body-bg-variant="bodyBgVariant"
    :body-text-variant="bodyTextVariant"
    :footer-bg-variant="footerBgVariant"
    :footer-text-variant="footerTextVariant">
    <b-container fluid>
        <b-row class="mb-1" v-for="(notification, index) in notifications" :key="index">
        <b-col>
        <b-alert variant="danger"
             dismissible
             :show="notification.showDismissibleAlert"
             @dismissed="cleanNotification(notification.seq)">
         {{ notification.content }}
        </b-alert>
        </b-col>
        </b-row>
    </b-container>
    <div slot="modal-footer" class="w-100">
      <b-btn size="sm" class="float-right m-1" variant="primary" @click="notificationModalShow=false">
        Close
      </b-btn>
    </div>
  </b-modal>

  <b-modal v-model="subscriptionModalShow"
    title="Subscriptions"
    :header-bg-variant="headerBgVariant"
    :header-text-variant="headerTextVariant"
    :body-bg-variant="bodyBgVariant"
    :body-text-variant="bodyTextVariant"
    :footer-bg-variant="footerBgVariant"
    :footer-text-variant="footerTextVariant">
    <b-container fluid>
        <b-row class="mb-1" v-for="(subscription, index) in subscriptions" :key="index">
        <b-col>
        <b-alert variant="danger"
             dismissible
             :show="subscription.showDismissibleAlert"
             @dismissed="cleanSubscription(subscription.seq)">
         {{ subscription.content }}
        </b-alert>
        </b-col>
        </b-row>
        
    </b-container>
    <div slot="modal-footer" class="w-100">
      <b-btn size="sm" class="float-right m-1" variant="primary" @click="subscriptionModalShow=false">
        Close
      </b-btn>
    </div>
  </b-modal>
</div>
</template>

<script>
import MqttService from '../service/MqttService'
import eventBus from '../service/eventBus'
var mqttService = new MqttService();

export default {
  name: 'DoggieNav',
  created() {
      console.log('call DoggieNav created');
      Date.prototype.format = function(fmt) { 
          var o = { 
              "M+" : this.getMonth()+1,
              "d+" : this.getDate(),
              "h+" : this.getHours(),
              "m+" : this.getMinutes(),
              "s+" : this.getSeconds(), 
              "q+" : Math.floor((this.getMonth()+3)/3),
              "S"  : this.getMilliseconds()
          }; 
          if(/(y+)/.test(fmt)) {
                  fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
          }
          for(var k in o) {
            if(new RegExp("("+ k +")").test(fmt)){
              fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
            }
          }
          return fmt; 
      }
      eventBus.$on('onConnectAction', this.onConnect);
      eventBus.$on('onFailureAction', this.onFailure);
      eventBus.$on('onConnectionLostAction', this.onConnectionLost);
      eventBus.$on('onMessageArrivedAction', message => {
        console.log(message);
        var item = {};
        item.seq = this.notificationSeq++;
        item.content = "[" + new Date().format("yyyy-MM-dd hh:mm:ss") + "] " + message;
        item.showDismissibleAlert = true;
        this.notifications.push(item);
        this.unreadNotifications++;
        this.viewNotification();
      });
      eventBus.$on('onSubscriptionCreationAction', sub => {
        var item = {};
        item.seq = this.subscriptionSeq++;
        item.content = "[" + new Date().format("yyyy-MM-dd hh:mm:ss") + "] " + sub;
        item.showDismissibleAlert = true;
        this.subscriptions.push(item);
      });
      this.notificationMenuText = "Notifications";
      this.connect();
     },

  data() {
    return {
    actionName : 'Connect',
    unreadNotifications : 0,
    notificationMenuText : "",
    
    unreadNotTipsShow : true,
    showDismissibleAlert : true,
    notifications : [
      
    ],
    notificationSeq : 0,
    notificationModalShow : false,
    subscriptions : [
      
    ],
    subscriptionSeq : 0,
    subscriptionModalShow : false,
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
  methods : {
    connect : function () {
      if (this.actionName == 'Connect') {
        mqttService.connect();
      } else {
        mqttService.disconnect();
      }
    },
    onConnect() {
      this.actionName = 'Disconnect';
    },
    onFailure() {
      this.actionName = 'Connect';
    },
    onConnectionLost() {
      this.actionName = 'Connect';
    },
    viewNotification() {
      this.unreadNotifications = 0;
      this.notificationModalShow = true;
    },
    cleanNotification(seq) {
      this.notifications.filter(item => item.seq != seq);
    },

    viewSubscriptions() {
      console.log("Call viewSubscriptions");
      this.subscriptionModalShow = true;
    },

    cleanSubscription(seq) {
      this.subscriptions.filter(item => item.seq != seq);
    },

    retrieveMyGps() {
      if ("geolocation" in navigator) {
        var geo_success = function (position) {
          console.log(position.coords.latitude);
          console.log(position.coords.longitude);
        }

        var geo_error = function () {
          alert("Sorry, no position available.");
        }

        var geo_options = {
          enableHighAccuracy: true, 
          maximumAge        : 30000, 
          timeout           : 2000
        };

        var wpid = navigator.geolocation.watchPosition(geo_success, geo_error, geo_options);
        console.log(wpid);
      } else {
        console.log("GPS not available");
      }
    }

  }
}
</script>

<style>

</style>
