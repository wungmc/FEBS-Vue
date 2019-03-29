<template>
  <a-button style="margin-right: .8rem" @click="onClose">取消</a-button>
</template>

<script>
export default {
  name: 'CancelButton',
  methods: {
    onClose () {
      this.$emit('close')
    }
  }
}
</script>
