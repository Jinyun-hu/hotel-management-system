<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="600px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="客人姓名" prop="guestName">
            <el-input v-model="form.guestName" placeholder="请输入客人姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话" prop="guestPhone">
            <el-input v-model="form.guestPhone" placeholder="请输入联系电话" />
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="房型" prop="roomTypeId">
            <el-select v-model="form.roomTypeId" placeholder="请选择房型" style="width: 100%" @change="handleRoomTypeChange">
              <el-option
                v-for="item in roomTypeOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="房间" prop="roomId">
            <el-select v-model="form.roomId" placeholder="请选择房间" style="width: 100%">
              <el-option
                v-for="item in roomOptions"
                :key="item.id"
                :label="item.roomNumber"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="入住日期" prop="checkInDate">
            <el-date-picker
              v-model="form.checkInDate"
              type="date"
              placeholder="选择入住日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
              :disabled-date="disableCheckInDate"
              @change="handleCheckInChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="退房日期" prop="checkOutDate">
            <el-date-picker
              v-model="form.checkOutDate"
              type="date"
              placeholder="选择退房日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
              :disabled-date="disableCheckOutDate"
              @change="handleCheckOutChange"
            />
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="订单状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%" @change="handleStatusChange">
              <el-option label="进行中" value="active" />
              <el-option label="已完成" value="completed" />
              <el-option label="待处理" value="pending" />
              <el-option label="已取消" value="canceled" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="支付状态" prop="paymentStatus">
            <el-select v-model="form.paymentStatus" placeholder="请选择支付状态" style="width: 100%" :disabled="isPaymentStatusDisabled">
              <el-option 
                v-for="option in paymentStatusOptions" 
                :key="option.value" 
                :label="option.label" 
                :value="option.value" 
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-form-item label="订单金额" prop="totalAmount">
        <el-input-number
          v-model="form.totalAmount"
          :min="0.01"
          :precision="2"
          :step="10"
          :disabled="isAmountAuto"
          style="width: 100%"
        />
        <div v-if="isAmountAuto && amountHint" class="amount-hint">
          <el-icon><InfoFilled /></el-icon>
          {{ amountHint }}
        </div>
      </el-form-item>
      
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注信息"
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import { addOrder, updateOrder, updateOrderPaymentStatus } from '@/api/order'
import { getRoomTypeList } from '@/api/roomType'
import { getRoomList } from '@/api/room'

const props = defineProps({
  modelValue: Boolean,
  type: {
    type: String,
    default: 'add'
  },
  data: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const formRef = ref(null)
const loading = ref(false)
const roomTypeOptions = ref([])
const roomOptions = ref([])
const isAmountAuto = ref(true)
const amountHint = ref('')

// 支付状态选项
const paymentStatusOptions = computed(() => {
  const currentStatus = form.status
  const allOptions = [
    { label: '已支付', value: 'paid' },
    { label: '未支付', value: 'unpaid' },
    { label: '已退款', value: 'refunded' }
  ]

  // 根据订单状态过滤可选择的支付状态
  if (currentStatus === 'canceled') {
    // 订单状态「已取消」时，不允许支付状态为「已支付」
    return allOptions.filter(option => option.value !== 'paid')
  } else if (currentStatus === 'completed') {
    // 订单状态「已完成」时，不允许支付状态为「已退款」
    return allOptions.filter(option => option.value !== 'refunded')
  } else if (currentStatus === 'active' || currentStatus === 'pending') {
    // 订单状态「进行中」或「待处理」时，不允许支付状态为「已退款」
    return allOptions.filter(option => option.value !== 'refunded')
  }
  return allOptions
})

// 支付状态是否禁用
const isPaymentStatusDisabled = computed(() => {
  // 当订单状态为「已取消」时，支付状态自动锁定
  return form.status === 'canceled'
})

// 订单状态变化时的处理
const handleStatusChange = async (newStatus) => {
  // 当订单状态改为「已完成」且支付状态是「未支付」时，弹窗提示
  if (newStatus === 'completed' && form.paymentStatus === 'unpaid') {
    try {
      await ElMessageBox.confirm(
        '该订单为挂账订单，请确认后续跟进收款',
        '确认提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
    } catch {
      // 如果用户取消，恢复原状态
      if (props.type === 'edit' && props.data) {
        form.status = props.data.status
      } else {
        form.status = 'pending'
      }
      return
    }
  }
  
  // 当订单状态改为「已取消」时，自动更新支付状态
  if (newStatus === 'canceled') {
    if (form.paymentStatus === 'paid') {
      form.paymentStatus = 'refunded'
      ElMessage.info('订单状态改为已取消，支付状态自动更新为已退款')
    } else if (form.paymentStatus === 'unpaid') {
      // 保持未支付状态
    }
  }
}

// 入住日期不可选今天之前
const disableCheckInDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 退房日期不可选入住日期之前（含当天）
const disableCheckOutDate = (time) => {
  if (form.checkInDate) {
    const checkInTime = new Date(form.checkInDate).getTime()
    return time.getTime() <= checkInTime
  }
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 入住日期变化时，如果退房日期早于入住日期则清空退房日期
const handleCheckInChange = (val) => {
  if (form.checkOutDate && val && form.checkOutDate <= val) {
    form.checkOutDate = ''
  }
  calculateAmount()
}

// 退房日期变化时触发金额计算
const handleCheckOutChange = () => {
  calculateAmount()
}

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const dialogTitle = computed(() => props.type === 'add' ? '新建订单' : '编辑订单')

const form = reactive({
  guestName: '',
  guestPhone: '',
  roomTypeId: null,
  roomId: null,
  checkInDate: '',
  checkOutDate: '',
  status: 'pending',
  paymentStatus: 'unpaid',
  totalAmount: 0,
  remark: ''
})

const rules = {
  guestName: [
    { required: true, message: '请输入客人姓名', trigger: 'blur' }
  ],
  guestPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  roomTypeId: [
    { required: true, message: '请选择房型', trigger: 'change' }
  ],
  roomId: [
    { required: true, message: '请选择房间', trigger: 'change' }
  ],
  checkInDate: [
    { required: true, message: '请选择入住日期', trigger: 'change' }
  ],
  checkOutDate: [
    { required: true, message: '请选择退房日期', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (value && form.checkInDate && value <= form.checkInDate) {
          callback(new Error('退房日期必须晚于入住日期'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  status: [
    { required: true, message: '请选择订单状态', trigger: 'change' }
  ],
  paymentStatus: [
    { required: true, message: '请选择支付状态', trigger: 'change' }
  ],
  totalAmount: [
    { required: true, message: '请输入订单金额', trigger: 'blur' }
  ]
}

// 获取房型列表（不分页，获取全部）
const fetchRoomTypes = async () => {
  try {
    const res = await getRoomTypeList({ page: 1, size: 1000 })
    const pageData = res.data || {}
    roomTypeOptions.value = pageData.data || []
  } catch (error) {
    console.error('获取房型列表失败:', error)
  }
}

// 获取房间列表
const fetchRooms = async (roomTypeId) => {
  try {
    const res = await getRoomList({ roomTypeId, status: 'available', page: 1, size: 1000 })
    const pageData = res.data || {}
    roomOptions.value = pageData.data || []
  } catch (error) {
    console.error('获取房间列表失败:', error)
  }
}

// 房型变化
const handleRoomTypeChange = (val) => {
  form.roomId = null
  if (val) {
    fetchRooms(val)
  } else {
    roomOptions.value = []
  }
  calculateAmount()
}

// 自动计算金额
const calculateAmount = () => {
  if (!isAmountAuto.value) return

  const roomType = roomTypeOptions.value.find(item => item.id === form.roomTypeId)
  if (!roomType || !roomType.price) {
    form.totalAmount = 0
    return
  }

  const price = parseFloat(roomType.price) || 0
  if (price <= 0) {
    form.totalAmount = 0
    return
  }

  if (form.checkInDate && form.checkOutDate) {
    const checkIn = new Date(form.checkInDate)
    const checkOut = new Date(form.checkOutDate)
    const nights = Math.ceil((checkOut - checkIn) / (1000 * 60 * 60 * 24))
    if (nights > 0) {
      form.totalAmount = Math.round(price * nights * 100) / 100
      amountHint.value = `${price}元/晚 × ${nights}晚 = ${form.totalAmount}元`
      return
    }
  }

  form.totalAmount = price
  amountHint.value = price ? `${price}元/晚` : ''
}

// 重置表单
const resetForm = () => {
  form.guestName = ''
  form.guestPhone = ''
  form.roomTypeId = null
  form.roomId = null
  form.checkInDate = ''
  form.checkOutDate = ''
  form.status = 'pending'
  form.paymentStatus = 'unpaid'
  form.totalAmount = 0
  form.remark = ''
  roomOptions.value = []
  isAmountAuto.value = true
  amountHint.value = ''
}

// 填充表单
const fillForm = (data) => {
  form.guestName = data.guestName
  form.guestPhone = data.guestPhone
  form.roomTypeId = data.roomTypeId
  form.roomId = data.roomId
  form.checkInDate = data.checkInDate
  form.checkOutDate = data.checkOutDate
  form.status = data.status
  form.paymentStatus = data.paymentStatus
  form.totalAmount = data.totalAmount
  form.remark = data.remark || ''
  if (data.roomTypeId) {
    fetchRooms(data.roomTypeId)
  }
}

// 关闭后重置
const handleClosed = () => {
  resetForm()
  formRef.value?.clearValidate()
}

// 提交
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  // 检查订单状态变更
  if (props.type === 'edit' && props.data) {
    const oldStatus = props.data.status
    const newStatus = form.status
    
    if (oldStatus !== newStatus) {
      // 根据状态变更给出相应提醒
      switch (newStatus) {
        case 'completed':
          // 检查退房日期是否已过
          const today = new Date().toISOString().split('T')[0]
          if (form.checkOutDate >= today) {
            if (!confirm('退房日期尚未结束，确定要标记为已完成吗？')) {
              return
            }
          }
          ElMessage.info('订单已完成，房间将自动释放')
          break
          
        case 'canceled':
          if (!confirm('确定要取消订单吗？房间将被释放。')) {
            return
          }
          ElMessage.info('订单已取消，房间已释放')
          break
          
        case 'active':
          if (form.paymentStatus !== 'paid') {
            ElMessage.warning('订单状态变为入住中，建议同时更新支付状态为已支付')
          }
          break
      }
    }
    
    // 检查支付状态变更
    const oldPaymentStatus = props.data.paymentStatus
    const newPaymentStatus = form.paymentStatus
    if (oldPaymentStatus !== newPaymentStatus) {
      if (newPaymentStatus === 'paid' && form.status === 'pending') {
        // 自动更新订单状态为 active
        form.status = 'active'
        ElMessage.info('支付状态已更新为已支付，订单状态自动更新为入住中')
      }
    }
  }
  
  loading.value = true
  try {
    if (props.type === 'add') {
      await addOrder(form)
      ElMessage.success('创建成功')
    } else {
      await updateOrder(props.data.id, form)
      ElMessage.success('更新成功')
    }
    visible.value = false
    emit('success')
  } catch (error) {
    // 显示更详细的错误信息
    let errorMsg = error.message || '操作失败'
    if (error.response && error.response.data && error.response.data.message) {
      errorMsg = error.response.data.message
    }
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}

watch(() => props.modelValue, (val) => {
  if (val) {
    fetchRoomTypes()
    if (props.type === 'edit' && props.data) {
      fillForm(props.data)
    } else {
      resetForm()
    }
  }
})

// 监听入住/退房日期变化，自动计算金额
watch([() => form.checkInDate, () => form.checkOutDate], () => {
  calculateAmount()
})
</script>

<style scoped>
.amount-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
