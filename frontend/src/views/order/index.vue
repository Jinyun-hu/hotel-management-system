<template>
  <div class="order-container">
    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="订单号">
          <el-input
            v-model="queryForm.orderNo"
            placeholder="请输入订单号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="客人姓名">
          <el-input
            v-model="queryForm.guestName"
            placeholder="请输入客人姓名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="房间号">
          <el-input
            v-model="queryForm.roomNumber"
            placeholder="请输入房间号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryForm.status" placeholder="全部状态" clearable>
            <el-option label="进行中" value="active" />
            <el-option label="已完成" value="completed" />
            <el-option label="待处理" value="pending" />
            <el-option label="已取消" value="canceled" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="queryForm.paymentStatus" placeholder="全部状态" clearable>
            <el-option label="已支付" value="paid" />
            <el-option label="未支付" value="unpaid" />
            <el-option label="已退款" value="refunded" />
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
        新建订单
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
        <el-table-column prop="orderNo" label="订单号" width="160" />
        <el-table-column prop="guestName" label="客人姓名" width="100" />
        <el-table-column prop="guestPhone" label="联系电话" width="120" />
        <el-table-column prop="roomTypeName" label="房型" width="120" />
        <el-table-column prop="roomNumber" label="房间号" width="80" align="center" />
        <el-table-column prop="checkInDate" label="入住日期" width="110" />
        <el-table-column prop="checkOutDate" label="退房日期" width="110" />
        <el-table-column prop="status" label="订单状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="支付状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getPaymentStatusType(row.paymentStatus)" size="small">
              {{ getPaymentStatusLabel(row.paymentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="140" align="right">
          <template #default="{ row }">
            <div class="amount-container">
              <span>¥{{ row.totalAmount }}</span>
              <el-tag v-if="row.status === 'completed' && row.paymentStatus === 'unpaid'" type="danger" size="small" class="receivable-tag">
                待收款
              </el-tag>
            </div>
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
    <order-dialog
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
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getOrderList, deleteOrder } from '@/api/order'
import OrderDialog from './components/OrderDialog.vue'

const loading = ref(false)
const tableData = ref([])

const queryForm = reactive({
  orderNo: '',
  guestName: '',
  roomNumber: '',
  status: '',
  paymentStatus: ''
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
  active: { label: '进行中', type: 'success' },
  completed: { label: '已完成', type: 'info' },
  pending: { label: '待处理', type: 'warning' },
  canceled: { label: '已取消', type: 'danger' }
}

const paymentStatusMap = {
  paid: { label: '已支付', type: 'success' },
  unpaid: { label: '未支付', type: 'warning' },
  refunded: { label: '已退款', type: 'info' }
}

const getStatusLabel = (status) => statusMap[status]?.label || status
const getStatusType = (status) => statusMap[status]?.type || ''
const getPaymentStatusLabel = (status) => paymentStatusMap[status]?.label || status
const getPaymentStatusType = (status) => paymentStatusMap[status]?.type || ''

// 获取订单列表
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      page: pagination.page,
      size: pagination.size
    }
    const res = await getOrderList(params)
    const pageData = res.data || {}
    tableData.value = pageData.data || []
    pagination.total = pageData.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
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
  queryForm.orderNo = ''
  queryForm.guestName = ''
  queryForm.roomNumber = ''
  queryForm.status = ''
  queryForm.paymentStatus = ''
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
  ElMessageBox.confirm(
    `确定要删除订单 "${row.orderNo}" 吗？此操作不可撤销。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteOrder(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
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
.order-container {
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
  
  .amount-container {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 4px;
  }
  
  .receivable-tag {
    margin-top: 4px;
  }
}
</style>
