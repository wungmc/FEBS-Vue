<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <a-form layout="horizontal">
        <div :class="advanced ? null: 'fold'">
          <a-row>
            <a-col :md="8" :sm="24">
              <a-form-item label="书名" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.name" />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="作者" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.author" />
              </a-form-item>
            </a-col>
          </a-row>
        </div>
        <span style="float: right; margin-top: 3px;">
          <a-button type="primary" @click="search">查询</a-button>
          <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          <a-button v-hasPermission="'book:add'" type="primary" ghost @click="add">新增</a-button>

          <!-- <a @click="toggleAdvanced" style="margin-left: 8px">
            {{advanced ? '收起' : '展开'}}
            <a-icon :type="advanced ? 'up' : 'down'" />
          </a> -->
        </span>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <!-- <a-button v-hasPermission="'book:delete'" @click="batchDelete">删除</a-button> -->
        <a-dropdown v-hasPermission="'book:export'">
          <a-menu slot="overlay">
            <a-menu-item key="export-data" @click="exportExcel">导出Excel</a-menu-item>
          </a-menu>
          <a-button>
            更多操作
            <a-icon type="down" />
          </a-button>
        </a-dropdown>
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo" :columns="columns" :dataSource="dataSource" :pagination="pagination" :loading="loading"
        @change="handleTableChange" :scroll="{ x: 900 }">
        <template slot="remark" slot-scope="text, record">
          <a-popover placement="topLeft">
            <template slot="content">
              <div style="max-width: 200px">{{text}}</div>
            </template>
            <p style="width: 200px;margin-bottom: 0">{{text}}</p>
          </a-popover>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon type="edit" v-hasPermission="'book:update'" theme="twoTone" twoToneColor="#4a9ff5"
            @click="edit(record)" title="修改书籍"></a-icon>
          <a-badge v-hasNoPermission="'book:update'" status="warning" text="无权限"></a-badge>
          <a-divider type="vertical" />
          <a-icon type="delete" theme="twoTone" v-hasPermission="'book:delete'" @click="deleteOne(record.id)"
            title="删除" />
        </template>
      </a-table>
    </div>
    <!-- 新增书籍 -->
    <book-add @close="handleBookAddClose" @success="handleBookAddSuccess"
     :bookAddVisiable="bookAddVisiable">
    </book-add>
    <!-- 修改书籍 -->
    <book-edit ref="bookEdit" @close="handleBookEditClose" @success="handleBookEditSuccess"
      :bookEditVisiable="bookEditVisiable">
    </book-edit>
  </a-card>
</template>

<script>
  import bookAdd from './BookAdd'
  import bookEdit from './BookEdit'

  export default {
    name: 'Book',
    components: {
      bookAdd,
      bookEdit
    },
    data() {
      return {
        advanced: false,
        dataSource: [],
        selectedRowKeys: [],
        selectedRows: null,
        paginationInfo: null,
        pagination: {
          pageSizeOptions: ['10', '20', '30', '40', '100'],
          defaultCurrent: 1,
          defaultPageSize: 10,
          showQuickJumper: true,
          showSizeChanger: true,
          showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
        },
        queryParams: {},
        bookAddVisiable: false,
        bookEditVisiable: false,
        loading: false
      }
    },
    computed: {
      columns() {
        return [{
          title: 'ID',
          dataIndex: 'id'
        }, {
          title: '书名',
          dataIndex: 'name'
        }, {
          title: '作者',
          dataIndex: 'author'
        }, {
          title: '出版日期',
          dataIndex: 'pulishDate'
        }, {
          title: '操作',
          dataIndex: 'operation',
          scopedSlots: {
            customRender: 'operation'
          },
          fixed: 'right',
          width: 100
        }]
      }
    },
    mounted() {
      this.fetch()
    },
    methods: {
      onSelectChange(selectedRowKeys, selectedRows) {
        this.selectedRowKeys = selectedRowKeys
        console.log(selectedRowKeys.length + ", " + (typeof selectedRows))
        console.log(selectedRows[0].author)
        for (let s of selectedRowKeys) {
          console.log(this.dataSource[s].id)
          console.log(this.dataSource[s].author)
        }
      },
      toggleAdvanced() {
        this.advanced = !this.advanced
        if (!this.advanced) {
          this.queryParams.fieldName = ''
        }
      },
      handleBookAddSuccess() {
        this.bookAddVisiable = false
        this.$message.success('新增书籍成功')
        this.search()
      },
      handleBookAddClose() {
        this.bookAddVisiable = false
      },
      add() {
        this.bookAddVisiable = true
      },
      handleBookEditSuccess() {
        this.bookEditVisiable = false
        this.$message.success('修改书籍成功')
        this.search()
      },
      handleBookEditClose() {
        this.bookEditVisiable = false
      },
      edit(record) {
        this.$refs.bookEdit.setFormValues(record)
        this.bookEditVisiable = true
      },
      batchDelete() {
        if (!this.selectedRowKeys.length) {
          this.$message.warning('请选择需要删除的记录')
          return
        }
        let that = this
        this.$confirm({
          title: '确定删除所选中的记录?',
          content: '当您点击确定按钮后，这些记录将会被彻底删除',
          centered: true,
          onOk() {
            let ids = []
            for (let key of that.selectedRowKeys) {
              ids.push(that.dataSource[key].id)
            }
            that.$delete('book/' + ids.join(',')).then(() => {
              that.$message.success('删除成功')
              that.selectedRowKeys = []
              that.search()
            })
          },
          onCancel() {
            that.selectedRowKeys = []
          }
        })
      },
      deleteOne(id) {
        console.log("delete=" + id)
        let that = this
        this.$confirm({
          title: '确定删除该记录吗?',
          content: '当您点击确定按钮后，该记录将会被彻底删除',
          centered: true,
          onOk() {
            that.$delete('book/' + id).then(() => {
              that.$message.success('删除成功')
              that.search()
            })
          }
        })
      },
      exportExcel() {
        this.$export('book/excel', {
          ...this.queryParams
        })
      },
      search() {
        this.fetch({
          ...this.queryParams
        })
      },
      reset() {
        // 取消选中
        this.selectedRowKeys = []
        // 重置分页
        this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
        if (this.paginationInfo) {
          this.paginationInfo.current = this.pagination.defaultCurrent
          this.paginationInfo.pageSize = this.pagination.defaultPageSize
        }
        this.paginationInfo = null
        // 重置查询参数
        this.queryParams = {}
        this.fetch()
      },
      handleTableChange(pagination, filters, sorter) {
        this.paginationInfo = pagination
        this.fetch({
          ...this.queryParams
        })
      },
      fetch(params = {}) {
        this.loading = true
        if (this.paginationInfo) {
          // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
          this.$refs.TableInfo.pagination.current = this.paginationInfo.current
          this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
          params.pageSize = this.paginationInfo.pageSize
          params.pageNum = this.paginationInfo.current
        } else {
          // 如果分页信息为空，则设置为默认值
          params.pageSize = this.pagination.defaultPageSize
          params.pageNum = this.pagination.defaultCurrent
        }
        this.$get('book', {
          ...params
        }).then((r) => {
          let data = r.data
          const pagination = {
            ...this.pagination
          }
          pagination.total = data.total
          this.loading = false
          this.dataSource = data.rows
          this.pagination = pagination
        })
      }
    }
  }

</script>

<style lang="less" scoped>
  @import "../../../../static/less/Common";

</style>
