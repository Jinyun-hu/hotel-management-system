<template>
  <div class="room-type-container">
    <!-- 搜索 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="房型名称">
          <el-input
            v-model="queryForm.name"
            placeholder="请输入房型名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部状态" clearable>
            <el-option label="启用" value="active" />
            <el-option label="停用" value="inactive" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 操作按钮 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增房型
      </el-button>
    </div>
    
    <!-- 数据表格 -->
    <el-card shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column label="序号" width="60" align="center">
          <template #default="{ $index }">
            {{ (pagination.page - 1) * pagination.size + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="房型图片" width="120" align="center">
          <template #default="{ row }">
            <el-image
              :src="getImageUrl(row.image)"
              fit="cover"
              style="width: 80px; height: 60px; border-radius: 4px;"
              :preview-src-list="[getImageUrl(row.image)]"
              preview-teleported
            >
              <template #error>
                <div class="image-error">
                  <el-icon :size="20"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="房型名称" width="150" />
        <el-table-column prop="capacity" label="可住人数" width="100" align="center" />
        <el-table-column prop="beds" label="床位配置" width="150" />
        <el-table-column prop="price" label="参考价格" width="120" align="right">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    
    <!-- 新增/编辑对话框 -->
    <room-type-dialog
      v-model="dialogVisible"
      :type="dialogType"
      :data="currentRow"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Picture } from '@element-plus/icons-vue'
import { getRoomTypeList, deleteRoomType, checkRoomTypeOrders } from '@/api/roomType'
import RoomTypeDialog from './components/RoomTypeDialog.vue'

const loading = ref(false)
const tableData = ref([])

// 获取完整图片URL
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  return url
}

const queryForm = reactive({
  name: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogType = ref('add')
const currentRow = ref(null)

// 获取房型列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      page: pagination.page,
      size: pagination.size
    }
    const res = await getRoomTypeList(params)
    const pageData = res.data || {}
    tableData.value = pageData.data || []
    pagination.total = pageData.total || 0
  } catch (error) {
    console.error('获取房型列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

// 重置
const handleReset = () => {
  queryForm.name = ''
  queryForm.status = ''
  pagination.page = 1
  fetchData()
}

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  currentRow.value = null
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  currentRow.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  // 先检查是否有订单关联
  checkRoomTypeOrders(row.id).then(async (res) => {
    const orders = res.data || []
    let confirmMessage = `确定要删除房型 "${row.name}" 吗？此操作不可撤销。`
    
    // 如果有订单关联，显示订单信息
    if (orders.length > 0) {
      confirmMessage += `\n\n该房型已关联 ${orders.length} 个订单：\n`
      orders.forEach((order, index) => {
        confirmMessage += `${index + 1}. 订单号：${order.orderNo}，客人：${order.guestName}，入住日期：${order.checkInDate}\n`
      })
      confirmMessage += '\n删除后，这些订单的房型信息将丢失，是否继续删除？'
    }
    
    ElMessageBox.confirm(
      confirmMessage,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      try {
        await deleteRoomType(row.id)
        ElMessage.success('删除成功')
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '删除失败')
      }
    })
  }).catch(error => {
    console.error('检查订单关联失败:', error)
    ElMessage.error('检查订单关联失败')
  })
}

// 操作成功
const handleSuccess = () => {
  fetchData()
}

// 分页
const handleSizeChange = (val) => {
  pagination.size = val
  pagination.page = 1
  fetchData()
}

const handlePageChange = (val) => {
  pagination.page = val
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.room-type-container {
  .search-card {
    margin-bottom: 20px;
  }
  
  .toolbar {
    margin-bottom: 20px;
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .image-error {
    width: 80px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f7fa;
    color: #c0c4cc;
    border-radius: 4px;
  }
}
</style>
