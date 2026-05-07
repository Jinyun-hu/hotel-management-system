import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据统计', icon: 'DataLine' }
      },
      {
        path: 'room-types',
        name: 'RoomTypes',
        component: () => import('@/views/room-type/index.vue'),
        meta: { title: '房型管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'rooms',
        name: 'Rooms',
        component: () => import('@/views/room/index.vue'),
        meta: { title: '房间管理', icon: 'House' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/order/index.vue'),
        meta: { title: '订单管理', icon: 'Document' }
      },
      {
        path: 'room-status',
        name: 'RoomStatus',
        component: () => import('@/views/room-status/index.vue'),
        meta: { title: '房态可视化', icon: 'View' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.meta.public) {
    next()
    return
  }
  
  const token = localStorage.getItem('token')
  if (!token) {
    next('/login')
    return
  }
  
  const userStore = useUserStore()
  if (!userStore.userInfo) {
    // 如果用户有 token 但 userInfo 为 null，自动获取用户信息
    userStore.fetchUserInfo().catch(() => {
      // 获取失败，跳转到登录页
      userStore.logout()
      next('/login')
    }).finally(() => {
      next()
    })
  } else {
    next()
  }
})

export default router
