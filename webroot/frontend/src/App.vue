<template>
  <img alt="Vue logo" src="./assets/logo.png">
  <ServicePoller msg="Service Poller"/>
</template>

<script>
import ServicePoller from './components/ServicePoller.vue'

export default {
  name: 'App',
  components: {
    ServicePoller
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
