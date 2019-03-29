<template>
  <a-drawer
    title="新增书籍"
    :maskClosable="true"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="bookAddVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='书名' v-bind="formItemLayout">
        <a-input style="width: 100%"
                 v-model="book.name"
                 v-decorator="['name',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='作者' v-bind="formItemLayout">
        <a-input v-model="book.author"
                 v-decorator="['author',
                   {rules: [
                    { required: true, message: '不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='出版日期' v-bind="formItemLayout">
        <!-- <a-input v-model="book.publishDate"
                 v-decorator="['publishDate',
                   {rules: [
                    { required: true, message: '不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/> -->
          <a-date-picker v-model="book.pulishDate" 
            showTime
            format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择出版日期"
          />
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <!-- <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm> -->
      <cancel-button @close="onClose" />
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>

import CancelButton from '@/components/CancelButton.vue'

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'BookAdd',
  components: {CancelButton},
  props: {
    bookAddVisiable: {
      default: false
    }
  },
  data () {
    return {
      loading: false,
      formItemLayout,
      form: this.$form.createForm(this),
      book: {}
    }
  },
  methods: {
    reset () {
      this.loading = false
      this.book = {}
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.$post('book', {
            ...this.book
          }).then(() => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>
