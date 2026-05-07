<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon size="28" color="#3ac487"><HomeFilled /></el-icon>
        <span class="title">酒店管理系统</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#ffffff"
        text-color="#2d3955"
        active-text-color="#3ac487"
      >
        <el-menu-item index="/dashboard" @click="navigateTo('/dashboard')">
          <el-icon><DataLine /></el-icon>
          <span>数据统计</span>
        </el-menu-item>

        <el-menu-item index="/room-types" @click="navigateTo('/room-types')">
          <el-icon><OfficeBuilding /></el-icon>
          <span>房型管理</span>
        </el-menu-item>

        <el-menu-item index="/rooms" @click="navigateTo('/rooms')">
          <el-icon><House /></el-icon>
          <span>房间管理</span>
        </el-menu-item>

        <el-menu-item index="/orders" @click="navigateTo('/orders')">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>

        <el-menu-item index="/room-status" @click="navigateTo('/room-status')">
          <el-icon><View /></el-icon>
          <span>房态可视化</span>
        </el-menu-item>

        <!-- 用户管理（仅超级管理员可见） -->
        <el-menu-item v-if="userStore.userInfo?.role === 'super_admin'" index="/users" @click="navigateTo('/users')">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <breadcrumb />
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" :icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.name || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
  
  <!-- 个人资料对话框 -->
  <profile-dialog v-model="profileDialogVisible" />
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, ArrowDown, DataLine, OfficeBuilding, House, Document, View, HomeFilled, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import Breadcrumb from './components/Breadcrumb.vue'
import ProfileDialog from './components/ProfileDialog.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const profileDialogVisible = ref(false)

const activeMenu = computed(() => route.path)

const navigateTo = (path) => {
  console.log('Navigating to:', path)
  router.push(path).catch(err => {
    console.error('Navigation error:', err)
  })
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      profileDialogVisible.value = true
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        router.push('/login')
        ElMessage.success('退出成功')
      })
      break
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  
  .sidebar {
    background-color: #ffffff;
    border-right: 1px solid #dde0ec;
    box-shadow: 0 0 10px 0 #e1e7f3;
    
    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      border-bottom: 1px solid #dde0ec;
      
      .title {
        font-size: 18px;
        font-weight: bold;
        color: #3ac487;
      }
    }
    
    .sidebar-menu {
      border-right: none;
      
      :deep(.el-menu-item) {
        height: 50px;
        line-height: 50px;
        
        &:hover {
          background-color: rgba(58, 196, 135, 0.1);
        }
        
        &.is-active {
          background-color: rgba(58, 196, 135, 0.1);
          border-left: 4px solid #3ac487;
        }
      }
    }
  }
  
  .header {
    height: 60px;
    background-color: #ffffff;
    border-bottom: 1px solid #dde0ec;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    
    .header-right {
      .user-info {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;
        padding: 5px 10px;
        border-radius: 4px;
        transition: background-color 0.3s;
        
        &:hover {
          background-color: #f5f7fa;
        }
        
        .username {
          font-size: 14px;
          color: #2d3955;
        }
      }
    }
  }
  
  .main-content {
    background-color: #f5f7fa;
    padding: 20px;
    overflow-y: auto;
  }
}
</style>
