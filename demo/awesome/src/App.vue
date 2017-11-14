<template>
<div>
<DoggieNavBar />
<DoggieNav />
<DoggieContent />
</div>
</template>

<script>
import DoggieNavBar from './components/DoggieNavBar.vue'
import DoggieNav from './components/DoggieNav.vue'
import DoggieContent from './components/DoggieContent.vue'

export default {
  name: 'app',
  components: {
    'DoggieNavBar' : DoggieNavBar,
    'DoggieNav' : DoggieNav,
    'DoggieContent' : DoggieContent
     }
}
</script>

<style>

</style>
