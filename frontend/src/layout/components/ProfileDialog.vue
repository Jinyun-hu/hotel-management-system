<template>
  <el-dialog
    v-model="visible"
    title="个人资料"
    width="450px"
    :close-on-click-modal="false"
  >
    <el-form :model="form" label-width="80px" ref="formRef" :rules="rules">
      <el-form-item label="头像">
        <div class="avatar-uploader">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :http-request="handleAvatarUpload"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="form.avatar" :src="form.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </div>
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="角色">
        <el-tag :type="form.role === 'super_admin' ? 'warning' : (form.role === 'admin' ? 'danger' : 'success')">
          {{ form.role === 'super_admin' ? '超级管理员' : (form.role === 'admin' ? '管理员' : '普通用户') }}
        </el-tag>
      </el-form-item>
      <el-form-item label="状态">
        <el-tag :type="form.status === 1 ? 'success' : 'info'">
          {{ form.status === 1 ? '启用' : '禁用' }}
        </el-tag>
      </el-form-item>
      <el-form-item label="修改时间">
        <span>{{ formatDate(form.updateTime) }}</span>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import axios from 'axios'

const props = defineProps({
  modelValue: Boolean
})

const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const form = ref({
  username: '',
  name: '',
  role: '',
  status: 1,
  avatar: '',
  updateTime: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '用户名长度在2-50个字符之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在2-50个字符之间', trigger: 'blur' }
  ]
}

watch(() => props.modelValue, (val) => {
  if (val && userStore.userInfo) {
    form.value = { ...userStore.userInfo }
  }
})

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleAvatarUpload = async (options) => {
  const file = options.file
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const token = localStorage.getItem('token')
    const response = await axios.post('/api/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': token ? `Bearer ${token}` : ''
      }
    })
    
    if (response.data.code === 1 || response.data.code === 200) {
      form.value.avatar = response.data.data.url
      ElMessage.success('头像上传成功')
      options.onSuccess(response.data)
    } else {
      ElMessage.error(response.data.message || '头像上传失败')
      options.onError(new Error(response.data.message || '头像上传失败'))
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
    options.onError(error)
  }
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式')
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB')
  }
  return isJPG && isLt2M
}

const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  if (!form.value.name.trim()) {
    ElMessage.warning('请输入姓名')
    return
  }

  loading.value = true
  try {
    await userStore.updateUserProfile({ 
      username: form.value.username, 
      name: form.value.name,
      avatar: form.value.avatar 
    })
    ElMessage.success('保存成功')
    visible.value = false
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.avatar-uploader {
  display: flex;
  align-items: center;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #d9d9d9;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-uploader-icon:hover {
  border-color: #1890ff;
  color: #1890ff;
}
</style>
