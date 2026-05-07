<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <el-icon size="48" color="#3ac487"><HomeFilled /></el-icon>
        <h1 class="title">酒店管理系统</h1>
        <p class="subtitle">欢迎登录</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="form.rememberMe">记住我</el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="goToRegister">立即注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, HomeFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: '123456',
  rememberMe: false
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  console.log('登录请求数据:', { username: form.username, password: form.password })
  
  loading.value = true
  try {
    await userStore.loginAction({
      username: form.username,
      password: form.password,
      rememberMe: form.rememberMe
    })
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.log('登录失败:', error)
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped lang="scss">
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #3ac487 0%, #0b9c64 100%);
  
  .login-box {
    width: 420px;
    padding: 40px;
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    
    .login-header {
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
    
    .login-form {
      .login-btn {
        width: 100%;
        background-color: #3ac487;
        border-color: #3ac487;
        
        &:hover {
          background-color: #0b9c64;
          border-color: #0b9c64;
        }
      }
      
      .login-footer {
        text-align: center;
        margin-top: 20px;
        font-size: 14px;
        color: #606266;
      }
    }
  }
}
</style>
