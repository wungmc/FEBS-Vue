<template>
  <div>
    Hello <span v-if="a">world</span>
    <a-button type="dashed" @click="handleclick">aniu</a-button>
    <a-time-picker use12Hours />
    <a-button type="danger">danger12</a-button>
  </div>
</template>

<script>
export default {
  name: "Test",
  data() {
    function data() {
      return {
        a: 1
      }
    }
    return {
      a: true,
      b: "",
      c: {}
    }
  },
  methods: {
    handleclick: function () {
      this.a = !this.a
    }

  }
}

</script>
