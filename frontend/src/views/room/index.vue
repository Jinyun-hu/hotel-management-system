<template>
  <div class="room-container">
    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="房间编号">
          <el-input
            v-model="queryForm.keyword"
            placeholder="请输入房间编号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="房型">
          <el-select v-model="queryForm.type" placeholder="全部房型" clearable>
            <el-option
              v-for="item in roomTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部状态" clearable>
            <el-option label="已入住" value="occupied" />
            <el-option label="空闲" value="available" />
            <el-option label="等待清洁" value="waiting_clean" />
            <el-option label="清洁中" value="cleaning" />
            <el-option label="维修中" value="maintenance" />
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
        新增房间
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
        <el-table-column prop="roomNumber" label="房间编号" width="100" />
        <el-table-column prop="roomTypeName" label="房型" width="120" />
        <el-table-column prop="floor" label="楼层" width="80" align="center" />
        <el-table-column prop="status" label="房间状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.price }}
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
    <room-dialog
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
import { Search, Plus, Edit, Delete, RefreshLeft } from '@element-plus/icons-vue'
import { getRoomList, addRoom, updateRoom, deleteRoom, checkRoomOrders } from '@/api/room'
import { getRoomTypeList } from '@/api/roomType'
import RoomDialog from './components/RoomDialog.vue'

const loading = ref(false)
const tableData = ref([])
const roomTypeOptions = ref([])

const queryForm = reactive({
  keyword: '',
  type: '',
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

// 状态映射
const statusMap = {
  occupied: { label: '已入住', type: 'success' },
  available: { label: '空闲', type: 'info' },
  waiting_clean: { label: '等待清洁', type: 'warning' },
  cleaning: { label: '清洁中', type: 'warning' },
  maintenance: { label: '维修中', type: 'danger' }
}

const getStatusLabel = (status) => statusMap[status]?.label || status
const getStatusType = (status) => statusMap[status]?.type || ''

// 获取房型列表
const fetchRoomTypes = async () => {
  try {
    const res = await getRoomTypeList({ page: 1, size: 1000 })
    const pageData = res.data || {}
    roomTypeOptions.value = (pageData.data || pageData || []).map(item => ({
      label: item.name,
      value: item.id
    }))
  } catch (error) {
    console.error('获取房型列表失败:', error)
  }
}

// 获取房间列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      page: pagination.page,
      size: pagination.size
    }
    const res = await getRoomList(params)
    const pageData = res.data || {}
    tableData.value = pageData.data || []
    pagination.total = pageData.total || 0
  } catch (error) {
    console.error('获取房间列表失败:', error)
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
  queryForm.keyword = ''
  queryForm.type = ''
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
  checkRoomOrders(row.id).then(async (res) => {
    const orders = res.data || []
    let confirmMessage = `确定要删除房间 "${row.roomNumber}" 吗？此操作不可撤销。`
    
    // 如果有订单关联，显示订单信息
    if (orders.length > 0) {
      confirmMessage += `\n\n该房间已关联 ${orders.length} 个订单：\n`
      orders.forEach((order, index) => {
        confirmMessage += `${index + 1}. 订单号：${order.orderNo}，客人：${order.guestName}，入住日期：${order.checkInDate}\n`
      })
      confirmMessage += '\n删除后，这些订单的房间信息将丢失，是否继续删除？'
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
        await deleteRoom(row.id)
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
  fetchRoomTypes()
  fetchData()
})
</script>

<style scoped lang="scss">
.room-container {
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
}
</style>
