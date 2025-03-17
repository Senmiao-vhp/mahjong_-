<template>
  <div class="game-main-container">
    <div class="game-header">
      <h1>游戏主界面</h1>
    </div>
    
    <div class="game-content">
      <div class="user-info-container">
        <div class="user-info">
          <h2>{{ userNickname }}</h2>
          <h3>{{ userId }}</h3>
        </div>
      </div>
      
      <div class="action-buttons">
        <el-button type="primary" class="action-button" @click="startGame">
          试玩
        </el-button>
        <el-button type="danger" class="action-button" @click="logout">
          退出登录
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { get } from '../utils/api'

const router = useRouter()
const userId = ref('')
const userNickname = ref('')
const loading = ref(false)

// 在组件挂载时获取用户信息
onMounted(async () => {
  // 从本地存储获取用户信息
  const storedUserId = localStorage.getItem('userId')
  const storedNickname = localStorage.getItem('userNickname')
  const token = localStorage.getItem('token')
  
  if (!storedUserId || !token) {
    ElMessage.warning('用户未登录，请先登录')
    router.push('/')
    return
  }
  
  // 先使用本地存储的信息
  userId.value = storedUserId
  userNickname.value = storedNickname || '加载中...'
  
  // 从后端获取最新用户信息
  await fetchUserInfo(parseInt(storedUserId))
})

// 从后端获取用户信息
const fetchUserInfo = async (id: number) => {
  loading.value = true
  
  try {
    const data = await get(`/users/${id}`)
    
    if (data.code === 200) {
      // 更新用户信息
      userNickname.value = data.data.nickname
      
      // 更新本地存储
      localStorage.setItem('userNickname', data.data.nickname)
    } else {
      console.error('获取用户信息失败:', data.msg)
      // 如果获取失败，继续使用本地存储的昵称
      
      // 如果是404错误（用户不存在），可能是因为数据库写入延迟，尝试延迟后重试
      if (data.code === 404) {
        setTimeout(() => {
          console.log('延迟1秒后重试获取用户信息')
          fetchUserInfo(id)
        }, 1000)
      }
    }
  } catch (error: any) {
    console.error('获取用户信息错误:', error)
    
    // 如果错误消息包含"不存在"，可能是因为数据库写入延迟，尝试延迟后重试
    if (error.message && (error.message.includes('不存在') || error.message.includes('请求的资源不存在'))) {
      ElMessage.info('正在同步用户信息，请稍候...')
      setTimeout(() => {
        console.log('延迟1秒后重试获取用户信息')
        fetchUserInfo(id)
      }, 1000)
    } else {
      ElMessage.error(`获取用户信息失败: ${error.message}`)
    }
  } finally {
    loading.value = false
  }
}

// 开始游戏
const startGame = () => {
  ElMessage.info('对局界面暂未实现')
}

// 登出
const logout = () => {
  // 清除登录状态
  localStorage.removeItem('userId')
  localStorage.removeItem('userNickname')
  localStorage.removeItem('token')
  
  router.push('/')
}
</script>

<style scoped>
.game-main-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  width: 100%;
}

.game-header {
  background-color: #f0f0e0;
  padding: 10px 20px;
  border-bottom: 1px solid #ddd;
}

.game-header h1 {
  margin: 0;
  font-size: 1.2rem;
  color: #666;
  font-weight: normal;
}

.game-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  width: 100%;
}

.user-info-container {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.user-info {
  text-align: right;
  padding: 10px 15px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.user-info h2 {
  margin: 0;
  font-size: 1.3rem;
  color: #333;
}

.user-info h3 {
  margin: 5px 0 0;
  font-size: 1rem;
  color: #666;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 15px;
}

.action-button {
  width: 48%;
  height: 50px;
  font-size: 1.1rem;
  border-radius: 4px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .game-content {
    padding: 10px;
  }
  
  .action-button {
    font-size: 1rem;
  }
}
</style> 