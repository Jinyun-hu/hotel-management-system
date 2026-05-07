<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <el-icon size="48" color="#3ac487"><HomeFilled /></el-icon>
        <h1 class="title">酒店管理系统</h1>
        <p class="subtitle">用户注册</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="register-form"
        @keyup.enter="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名（3-20字符）"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入真实姓名"
            size="large"
            :prefix-icon="Avatar"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（6-20字符）"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        

        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-btn"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
        
        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="goToLogin">立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Avatar, HomeFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  name: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],

}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const { confirmPassword, ...registerData } = form
    await userStore.registerAction(registerData)
    ElMessage.success('注册成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped lang="scss">
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #3ac487 0%, #0b9c64 100%);
  
  .register-box {
    width: 420px;
    padding: 40px;
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    
    .register-header {
      text-align: center;
      margin-bottom: 30px;
      
      .title {
        font-size: 24px;
        font-weight: bold;
        color: #2d3955;
        margin-top: 15px;
        margin-bottom: 8px;
      }
      
      .subtitle {
        font-size: 14px;
        color: #909399;
      }
    }
    
    .register-form {
      .role-select {
        width: 100%;
      }
      
      .register-btn {
        width: 100%;
        background-color: #3ac487;
        border-color: #3ac487;
        
        &:hover {
          background-color: #0b9c64;
          border-color: #0b9c64;
        }
      }
      
      .register-footer {
        text-align: center;
        margin-top: 20px;
        font-size: 14px;
        color: #606266;
      }
    }
  }
}
</style>
