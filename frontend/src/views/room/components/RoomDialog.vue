<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="500px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="房间编号" prop="roomNumber">
        <el-input
          v-model="form.roomNumber"
          placeholder="请输入房间编号"
          maxlength="20"
          show-word-limit
        />
      </el-form-item>
      
      <el-form-item label="所属房型" prop="roomTypeId">
        <el-select v-model="form.roomTypeId" placeholder="请选择房型" style="width: 100%">
          <el-option
            v-for="item in roomTypeOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="楼层" prop="floor">
        <el-input-number
          v-model="form.floor"
          :min="1"
          :max="100"
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item label="价格" prop="price">
        <el-input-number
          v-model="form.price"
          :min="0.01"
          :precision="2"
          :step="10"
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item label="房间状态" prop="status">
        <el-select 
          v-model="form.status" 
          placeholder="请选择状态" 
          style="width: 100%"
        >
          <el-option 
            v-for="option in statusOptions" 
            :key="option.value" 
            :label="option.label" 
            :value="option.value"
            :disabled="statusOptionDisabled(option.value)"
          />
        </el-select>
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
import { ElMessage } from 'element-plus'
import { addRoom, updateRoom } from '@/api/room'
import { getRoomTypeList } from '@/api/roomType'

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

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const dialogTitle = computed(() => props.type === 'add' ? '新增房间' : '编辑房间')

const form = reactive({
  roomNumber: '',
  roomTypeId: null,
  floor: 1,
  price: 0,
  status: 'available'
})

// 状态选项
const statusOptions = computed(() => {
  return [
    { label: '空闲', value: 'available' },
    { label: '等待清洁', value: 'waiting_clean' },
    { label: '清洁中', value: 'cleaning' },
    { label: '维修中', value: 'maintenance' }
  ]
})

// 状态是否禁用
const isStatusDisabled = (status) => {
  // 当状态为已入住时，检查是否有订单
  if (status === 'occupied') {
    // 这里应该根据实际情况检查是否有订单
    // 暂时返回false，由后端进行验证
    return false
  }
  // 当状态为已入住时，检查是否有客人入住
  return form.status === 'occupied'
}

// 状态选项的禁用状态
const statusOptionDisabled = (status) => {
  // 入住状态不可以手动选择，由系统根据订单状态自动管理
  if (status === 'occupied') {
    return true
  }
  return false
}

const rules = {
  roomNumber: [
    { required: true, message: '请输入房间编号', trigger: 'blur' },
    { max: 20, message: '房间编号长度不能超过20个字符', trigger: 'blur' }
  ],
  roomTypeId: [
    { required: true, message: '请选择房型', trigger: 'change' }
  ],
  floor: [
    { required: true, message: '请输入楼层', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择房间状态', trigger: 'change' }
  ]
}

// 获取房型列表
const fetchRoomTypes = async () => {
  try {
    const res = await getRoomTypeList({ page: 1, size: 1000 })
    const pageData = res.data || {}
    roomTypeOptions.value = pageData.data || pageData || []
  } catch (error) {
    console.error('获取房型列表失败:', error)
  }
}

// 重置表单
const resetForm = () => {
  form.roomNumber = ''
  form.roomTypeId = null
  form.floor = 1
  form.price = 0
  form.status = 'available'
}

// 填充表单
const fillForm = (data) => {
  form.roomNumber = data.roomNumber
  form.roomTypeId = data.roomTypeId
  form.floor = data.floor
  form.price = data.price
  form.status = data.status
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
  
  loading.value = true
  try {
    if (props.type === 'add') {
      await addRoom(form)
      ElMessage.success('添加成功')
    } else {
      await updateRoom(props.data.id, form)
      ElMessage.success('更新成功')
    }
    visible.value = false
    emit('success')
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
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
</script>
