import Vue from 'vue'
import App from './App.vue'
import router from './router'


import BootstrapVue from 'bootstrap-vue';

Vue.use(BootstrapVue);


new Vue({
  router,
  template: '<App/>',
  components: { 
  	App
  	 }
}).$mount('#app');