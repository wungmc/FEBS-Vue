<template>
  <a-drawer
    title="修改书籍"
    :maskClosable="true"
    width=650
    placement="right"
    :closable="false"
    @close="onClose"
    :visible="bookEditVisiable"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">
    <a-form :form="form">
      <a-form-item label='书名' v-bind="formItemLayout">
        <a-input style="width: 100%"
                        v-decorator="['name',
                   {rules: [
                    { required: true, message: '不能为空'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='作者' v-bind="formItemLayout">
        <a-input v-decorator="['author',
                   {rules: [
                    { required: true, message: '不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/>
      </a-form-item>
      <a-form-item label='出版日期' v-bind="formItemLayout">
        <!-- <a-input v-decorator="['pulishDate',
                   {rules: [
                    { required: true, message: '不能为空'},
                    { max: 20, message: '长度不能超过20个字符'}
                  ]}]"/> -->
        <a-date-picker v-decorator="['pulishDate']"
          showTime
          format="YYYY-MM-DD HH:mm:ss"
          placeholder="Select Time"
        />
      </a-form-item>
    </a-form>
    <div class="drawer-bootom-button">
      <!-- <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消"> -->
        <!-- <a-button style="margin-right: .8rem" @click="onClose">取消</a-button> -->
      <!-- </a-popconfirm> -->
      <cancel-button @close="onClose" />
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>
<script>

import CancelButton from '@/components/CancelButton.vue'
import moment from 'moment'
moment.locale('zh-cn')

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 18 }
}
export default {
  name: 'BookEdit',
  components: {CancelButton},
  props: {
    bookEditVisiable: {
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
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    setFormValues ({...book}) {
      let fields = ['name', 'author', 'pulishDate']
      Object.keys(book).forEach((key) => {
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          let obj = {}
          obj[key] = book[key]
          if (key === 'pulishDate') {
            this.form.setFieldsValue({pulishDate: moment(book[key])})  
          }
          else {
            this.form.setFieldsValue(obj)
          }
        }
      })
      this.book.id = book.id
    },
    handleSubmit () {
      this.form.validateFields((err, values) => {
        if (!err) {
          let book = this.form.getFieldsValue()
          book.id = this.book.id
          this.$put('book', {
            ...book
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
