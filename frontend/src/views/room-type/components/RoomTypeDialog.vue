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
      <el-form-item label="房型名称" prop="name">
        <el-input
          v-model="form.name"
          placeholder="请输入房型名称"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>
      
      <el-form-item label="可住人数" prop="capacity">
        <el-input-number
          v-model="form.capacity"
          :min="1"
          :max="20"
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item label="床位配置" prop="beds">
        <el-input
          v-model="form.beds"
          placeholder="例如：1张特大床"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>
      
      <el-form-item label="参考价格" prop="price">
        <el-input-number
          v-model="form.price"
          :min="0.01"
          :precision="2"
          :step="10"
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio value="active">启用</el-radio>
          <el-radio value="inactive">停用</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item label="房型图片" prop="image">
        <el-upload
          class="image-uploader"
          :action="''"
          :http-request="handleUpload"
          :show-file-list="false"
          accept="image/*"
          :before-upload="beforeUpload"
        >
          <img v-if="form.image" :src="getImageUrl(form.image)" class="uploaded-image" />
          <div v-else class="upload-placeholder">
            <el-icon :size="28"><Plus /></el-icon>
            <span>点击上传图片</span>
          </div>
        </el-upload>
        <div v-if="form.image" class="image-actions">
          <el-button type="danger" size="small" link @click="removeImage">删除图片</el-button>
        </div>
        <div class="upload-tip">支持 jpg/png/gif/webp 格式，大小不超过 5MB</div>
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
import { Plus } from '@element-plus/icons-vue'
import { addRoomType, updateRoomType, uploadImage } from '@/api/roomType'

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

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const dialogTitle = computed(() => props.type === 'add' ? '新增房型' : '编辑房型')

const form = reactive({
  name: '',
  capacity: 2,
  beds: '',
  price: 0,
  status: 'active',
  image: ''
})

const rules = {
  name: [
    { required: true, message: '请输入房型名称', trigger: 'blur' },
    { max: 50, message: '房型名称长度不能超过50个字符', trigger: 'blur' }
  ],
  capacity: [
    { required: true, message: '请输入可住人数', trigger: 'blur' }
  ],
  beds: [
    { required: true, message: '请输入床位配置', trigger: 'blur' },
    { max: 50, message: '床位配置长度不能超过50个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入参考价格', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 重置表单
const resetForm = () => {
  form.name = ''
  form.capacity = 2
  form.beds = ''
  form.price = 0
  form.status = 'active'
  form.image = ''
}

// 获取完整图片URL
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  return url
}

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

// 上传图片
const handleUpload = async ({ file }) => {
  try {
    const res = await uploadImage(file)
    form.image = res.data.url
    ElMessage.success('图片上传成功')
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败')
  }
}

// 删除图片
const removeImage = () => {
  form.image = ''
}

// 填充表单
const fillForm = (data) => {
  form.name = data.name
  form.capacity = data.capacity
  form.beds = data.beds
  form.price = data.price
  form.status = data.status
  form.image = data.image || ''
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
      await addRoomType(form)
      ElMessage.success('添加成功')
    } else {
      await updateRoomType(props.data.id, form)
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
    if (props.type === 'edit' && props.data) {
      fillForm(props.data)
    } else {
      resetForm()
    }
  }
})
</script>

<style scoped>
.image-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
  display: inline-block;
}

.image-uploader:hover {
  border-color: #409eff;
}

.upload-placeholder {
  width: 200px;
  height: 150px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #8c939d;
  font-size: 14px;
  background-color: #fafafa;
}

.uploaded-image {
  width: 200px;
  height: 150px;
  object-fit: cover;
  display: block;
}

.image-actions {
  margin-top: 8px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
