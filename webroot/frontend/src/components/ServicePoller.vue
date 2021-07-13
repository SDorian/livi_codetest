<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <input v-model="name" name ="name" placeholder="Service name" />
    <input v-model="url" name="url" placeholder="Service url" />
    <button @click.prevent="save()">Save</button>

    <ul>
      <li v-for="(service, index) in services" v-bind:key="index">
        {{ service.name }} - {{ service.url }} : {{ service.status }} ({{ service.createdAt }}) <button @click.prevent="remove(index)">Delete</button>
      </li>
    </ul>
  </div>
</template>

<script>
import { onMounted, reactive,toRefs } from 'vue'
import axios from 'axios';
export default {
  name: 'ServicePoller',
  props: {
    msg: String
  },
  setup() {
    const state = reactive({
      url: '',
      name: '',
      services: []
    });
    setInterval(() => axios.get('http://localhost:3000/service')
        .then(response => state.services = response.data), 3000);

    onMounted(async () => {
      await axios.get('http://localhost:3000/service')
        .then(response => state.services = response.data)
    });

    async function save(){
      const payload = {
        'url': state.url,
        'name': state.name
      }

      await axios.post('http://localhost:3000/service', payload)
        .then(response => {
          state.services.push(response.data);
          state.url = '';
          state.name = '';
        })
    }

    async function remove(index) {
      await axios.delete('http://localhost:3000/service/' +index)
        .then(state.services.splice(index, 1));
    }

    return {
      ...toRefs(state),
      save,
      remove
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  text-align: ;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
