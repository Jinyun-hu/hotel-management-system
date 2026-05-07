<template>
  <div class="error-container">
    <div class="error-content">
      <h1 class="error-code">404</h1>
      <p class="error-message">页面不存在</p>
      <p class="error-desc">抱歉，您访问的页面不存在或已被移除</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped lang="scss">
.error-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  
  .error-content {
    text-align: center;
    
    .error-code {
      font-size: 120px;
      font-weight: bold;
      color: #3ac487;
      margin-bottom: 20px;
      line-height: 1;
    }
    
    .error-message {
      font-size: 24px;
      color: #2d3955;
      margin-bottom: 10px;
    }
    
    .error-desc {
      font-size: 14px;
      color: #909399;
      margin-bottom: 30px;
    }
  }
}
</style>
