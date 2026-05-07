<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAddUser">新增用户</el-button>
        </div>
      </template>
      
      <el-table :data="userList" style="width: 100%" border>
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column prop="name" label="姓名" width="180" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'super_admin' ? 'warning' : (scope.row.role === 'admin' ? 'danger' : 'success')">
              {{ scope.row.role === 'super_admin' ? '超级管理员' : (scope.row.role === 'admin' ? '管理员' : '普通用户') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditUser(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="80px" ref="formRef" :rules="rules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="!isEdit">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" :disabled="!isSuperAdmin && form.role === 'super_admin'">
            <el-option label="管理员" value="admin" />
            <el-option v-if="isSuperAdmin" label="超级管理员" value="super_admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const dialogVisible = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const formRef = ref(null)
const userList = ref([])

// 判断当前用户是否为超级管理员
const isSuperAdmin = computed(() => {
  return userStore.userInfo?.role === 'super_admin'
})

const form = ref({
  id: '',
  username: '',
  name: '',
  password: '',
  confirmPassword: '',
  role: 'user',
  status: 1
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '用户名长度在2-50个字符之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在2-50个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.value.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

onMounted(async () => {
  await fetchUserList()
})

const fetchUserList = async () => {
  try {
    // 调用后端接口获取用户列表
    const response = await fetch('/api/auth/users', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      }
    })
    const data = await response.json()
    if (data.code === 1 || data.code === 200) {
      userList.value = data.data
    } else {
      throw new Error(data.message || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '获取用户列表失败')
  }
}

const handleAddUser = () => {
  isEdit.value = false
  form.value = {
    id: '',
    username: '',
    name: '',
    password: '',
    confirmPassword: '',
    role: 'admin',
    status: 1
  }
  dialogVisible.value = true
}

const handleEditUser = (user) => {
  isEdit.value = true
  form.value = {
    id: user.id,
    username: user.username,
    name: user.name,
    password: '',
    confirmPassword: '',
    role: user.role,
    status: user.status
  }
  dialogVisible.value = true
}

const handleDeleteUser = async (userId) => {
  try {
    // 弹出确认对话框
    if (!confirm('确定要删除该用户吗？')) {
      return
    }
    
    // 调用后端接口删除用户
    const response = await fetch(`/api/auth/users/${userId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      }
    })
    const data = await response.json()
    if (data.code === 1 || data.code === 200) {
      ElMessage.success('删除成功')
      await fetchUserList()
    } else {
      throw new Error(data.message || '删除失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '删除失败')
  }
}

const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  loading.value = true
  try {
    // 检查用户名是否已存在
    if (!isEdit.value) {
      const checkResponse = await fetch('/api/auth/users', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      })
      const checkData = await checkResponse.json()
      if (checkData.code === 1 || checkData.code === 200) {
        const existingUser = checkData.data.find(user => user.username === form.value.username)
        if (existingUser) {
          throw new Error('用户名已存在')
        }
      }
    }

    // 调用后端接口保存用户
    let response
    if (isEdit.value) {
      // 更新用户
      response = await fetch(`/api/auth/users/${form.value.id}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(form.value)
      })
    } else {
      // 新增用户
      response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(form.value)
      })
    }
    const data = await response.json()
    if (data.code === 1 || data.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
      dialogVisible.value = false
      await fetchUserList()
    } else {
      throw new Error(data.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>