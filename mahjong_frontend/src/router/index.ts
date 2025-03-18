import { createRouter, createWebHistory } from 'vue-router'
import { tokenUtils } from '../utils/api'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { 
      requiresAuth: false 
    }
  },
  {
    path: '/game',
    name: 'GameMain',
    component: () => import('../views/GameMain.vue'),
    meta: { 
      requiresAuth: true 
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 添加全局前置守卫
router.beforeEach((to, from, next) => {
  // 检查目标路由是否需要认证
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  if (requiresAuth) {
    // 获取令牌和用户信息
    const token = tokenUtils.getToken()
    const userInfo = tokenUtils.getUserInfo()
    
    if (!token || !userInfo) {
      // 如果没有令牌或无法获取用户信息，重定向到登录页
      ElMessage.warning('用户未登录或会话已过期，请先登录')
      next({ path: '/' })
      return
    }
  }
  
  // 继续导航
  next()
})

export default router 