<template>
  <div class="hello">
      <div >
          index
          <footer-bar></footer-bar>
      </div>
  </div>
</template>

<script>
import FooterBar from '../components/footerBar.vue'
export default {
  components:{FooterBar},
  data() {
	return {
	  isShowSize: false,
	  i: -1
	};
  },

  methods: {

  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}

</style>
