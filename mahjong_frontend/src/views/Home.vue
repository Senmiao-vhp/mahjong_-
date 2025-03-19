<template>
  <div class="home-container">
    <div class="title-container">
      <h1 class="game-title">雀鬼</h1>
    </div>
    
    <div class="login-section">
      <h2 class="login-title">游客登录</h2>
      
      <!-- API状态提示 -->
      <div class="api-status" :class="{ 'api-connected': apiConnected, 'api-disconnected': !apiConnected }">
        <div class="status-text">{{ apiStatus }}</div>
        
        <div v-if="apiConnected && apiDetails" class="api-details">
          <div><strong>服务名称:</strong> {{ apiDetails.service }}</div>
          <div><strong>版本:</strong> {{ apiDetails.version }}</div>
          <div><strong>服务器时间:</strong> {{ apiDetails.timestamp }}</div>
        </div>
        
        <div v-if="!apiConnected && apiTestResults" class="api-error-details">
          <el-button type="text" @click="toggleDetailedResults" class="details-toggle">
            {{ showDetailedResults ? '隐藏详细信息' : '显示详细信息' }}
          </el-button>
          
          <div v-if="showDetailedResults" class="detailed-results">
            <div class="test-result">
              <div><strong>健康检查:</strong> {{ apiTestResults.health?.ok ? '成功' : '失败' }}</div>
              <div v-if="!apiTestResults.health?.ok">
                {{ apiTestResults.health?.error || `${apiTestResults.health?.status} ${apiTestResults.health?.statusText}` }}
              </div>
            </div>
            
            <div class="test-result">
              <div><strong>游客登录端点:</strong> {{ apiTestResults.guest?.ok ? '成功' : '失败' }}</div>
              <div v-if="!apiTestResults.guest?.ok">
                {{ apiTestResults.guest?.error || `${apiTestResults.guest?.status} ${apiTestResults.guest?.statusText}` }}
              </div>
            </div>
            
            <div class="test-result">
              <div><strong>昵称检查端点:</strong> {{ apiTestResults.checkNickname?.ok ? '成功' : '失败' }}</div>
              <div v-if="!apiTestResults.checkNickname?.ok">
                {{ apiTestResults.checkNickname?.error || `${apiTestResults.checkNickname?.status} ${apiTestResults.checkNickname?.statusText}` }}
              </div>
            </div>
          </div>
        </div>
        
        <div class="api-actions">
          <el-button v-if="!apiTestInProgress" 
                    type="primary" 
                    size="small" 
                    @click="checkApiConnection" 
                    class="action-button">
            重试连接
          </el-button>
          
          <el-button type="warning" 
                    size="small" 
                    @click="testDirectApi" 
                    class="action-button">
            直接测试API
          </el-button>
          
          <el-button 
            :type="mockApiEnabled ? 'success' : 'info'" 
            size="small" 
            @click="toggleMockApi" 
            class="action-button">
            {{ mockApiEnabled ? '已启用模拟API' : '启用模拟API' }}
          </el-button>
        </div>
        
        <!-- API测试结果 -->
        <div v-if="directTestResult" class="direct-test-result">
          <div><strong>测试结果:</strong> {{ directTestResult.success ? '成功' : '失败' }}</div>
          <div><strong>状态码:</strong> {{ directTestResult.status }}</div>
          <div v-if="directTestResult.data">
            <strong>数据:</strong> 
            <pre>{{ JSON.stringify(directTestResult.data, null, 2) }}</pre>
          </div>
          <div v-if="directTestResult.error">
            <strong>错误:</strong> {{ directTestResult.error }}
          </div>
        </div>
      </div>
      
      <div class="button-group">
        <el-button type="success" class="login-button" @click="guestLogin" :disabled="!apiConnected && !mockApiEnabled">
          游客登录
        </el-button>
        <el-button type="primary" class="login-button" @click="showRegisterDialog" :disabled="!apiConnected && !mockApiEnabled">
          注册新ID并登录
        </el-button>
        <el-button type="primary" class="login-button" @click="showLoginDialog" :disabled="!apiConnected && !mockApiEnabled">
          使用已有ID登录
        </el-button>
      </div>
    </div>

    <!-- 注册新ID对话框 -->
    <el-dialog
      v-model="registerDialogVisible"
      title="注册新ID"
      width="30%"
      :show-close="false"
      center
    >
      <div class="dialog-content">
        <p>请输入八个字以内的昵称</p>
        <el-input v-model="nickname" placeholder="请输入" maxlength="8" show-word-limit />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelRegister">取消</el-button>
          <el-button type="primary" @click="confirmNickname" :disabled="!nickname">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 确认昵称对话框 -->
    <el-dialog
      v-model="confirmDialogVisible"
      title="确认昵称"
      width="30%"
      :show-close="false"
      center
    >
      <div class="dialog-content">
        <p>"{{ nickname }}"</p>
        <p>是否以此昵称创建新的ID?</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="backToRegister">取消</el-button>
          <el-button type="primary" @click="createUser">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 创建成功对话框 -->
    <el-dialog
      v-model="successDialogVisible"
      title="返回的ID"
      width="30%"
      :show-close="false"
      center
    >
      <div class="dialog-content">
        <p>已登录，请备份ID以防丢失</p>
        <p class="user-id">{{ userId }}</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="enterGame">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 昵称被占用对话框 -->
    <el-dialog
      v-model="failDialogVisible"
      title="创建失败"
      width="30%"
      :show-close="false"
      center
    >
      <div class="dialog-content">
        <p>该昵称已被占用，请尝试其他昵称</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="backToRegister">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 登录对话框 -->
    <el-dialog
      v-model="loginDialogVisible"
      title="登录"
      width="30%"
      :show-close="false"
      center
    >
      <div class="dialog-content">
        <p>输入ID (8位数字)</p>
        <el-input 
          v-model="userId" 
          placeholder="请输入" 
          maxlength="8" 
          @input="validateUserId"
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelLogin">取消</el-button>
          <el-button type="primary" @click="login" :disabled="!isValidUserId">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { get, post, testApiConnection, fallbackTestApiConnection, comprehensiveApiTest, API_BASE_URL, tokenUtils } from '../utils/api'
import { ENABLE_MOCK_API, setMockApiEnabled } from '../utils/mockApi'

// 定义API测试结果的类型
interface ApiTestResult {
  ok: boolean;
  status?: number;
  statusText?: string;
  data?: any;
  error?: string;
}

// 定义API详情的类型
interface ApiDetails {
  service: string;
  version: string;
  timestamp: string;
  status: string;
}

// 定义综合测试结果的类型
interface ComprehensiveTestResult {
  health: ApiTestResult;
  guest: ApiTestResult;
  checkNickname: ApiTestResult;
  overall: boolean;
  error?: string;
}

// 定义直接测试结果的类型
interface DirectTestResult {
  success: boolean;
  status?: number;
  data?: any;
  error?: string;
}

const router = useRouter()
const apiStatus = ref('正在检查API连接...')
const apiConnected = ref(false)
const apiTestInProgress = ref(true)
const apiDetails = ref<ApiDetails | null>(null)
const apiTestResults = ref<ComprehensiveTestResult | null>(null)
const showDetailedResults = ref(false)
const directTestResult = ref<DirectTestResult | null>(null)

// 对话框显示状态
const registerDialogVisible = ref(false)
const confirmDialogVisible = ref(false)
const successDialogVisible = ref(false)
const failDialogVisible = ref(false)
const loginDialogVisible = ref(false)

// 表单数据
const nickname = ref('')
const userId = ref('')
const isValidUserId = ref(false)

// 模拟API状态
const mockApiEnabled = ref(ENABLE_MOCK_API)

// 在组件挂载时检查API连接
onMounted(async () => {
  await checkApiConnection()
})

// 检查API连接
const checkApiConnection = async () => {
  apiTestInProgress.value = true
  apiStatus.value = '正在检查API连接...'
  apiDetails.value = null
  apiTestResults.value = null
  showDetailedResults.value = false
  
  try {
    // 使用综合测试
    const results = await comprehensiveApiTest()
    apiTestResults.value = results
    
    if (results.overall) {
      apiStatus.value = '后端API连接正常'
      apiConnected.value = true
      
      // 如果健康检查成功并有数据，保存详情
      if (results.health && results.health.ok && results.health.data && results.health.data.data) {
        apiDetails.value = {
          service: results.health.data.data.service || 'Mahjong Game API',
          version: results.health.data.data.version || '1.0.0',
          timestamp: results.health.data.data.timestamp || new Date().toLocaleString(),
          status: results.health.data.data.status || 'UP'
        }
      }
    } else {
      apiStatus.value = '后端API连接失败'
      apiConnected.value = false
      ElMessage.warning(`无法连接到后端API (${API_BASE_URL})，请确保后端服务已启动`)
    }
  } catch (error: any) {
    apiStatus.value = `后端API连接错误: ${error.message}`
    apiConnected.value = false
    ElMessage.error('检查API连接时发生错误')
  } finally {
    apiTestInProgress.value = false
  }
}

// 切换显示详细测试结果
const toggleDetailedResults = () => {
  showDetailedResults.value = !showDetailedResults.value
}

// 游客登录
const guestLogin = async () => {
  if (!apiConnected.value && !mockApiEnabled.value) {
    ElMessage.warning('后端API未连接，无法登录')
    return
  }
  
  try {
    // 显示加载状态
    const loading = ElMessage({
      message: '正在登录...',
      type: 'info',
      duration: 0
    })
    
    const data = await post('/users/guest')
    
    // 关闭加载提示
    loading.close()
    
    if (data.code === 200) {
      // 保存令牌和用户信息
      tokenUtils.setToken(data.data.token);
      tokenUtils.setUserInfo(data.data.id, data.data.nickname);
      
      ElMessage.success('登录成功')
      
      // 导航到游戏主界面
      router.push('/game')
    } else {
      ElMessage.error(data.msg || '登录失败')
    }
  } catch (error: any) {
    console.error('游客登录错误:', error)
    ElMessage.error(`登录失败: ${error.message}`)
  }
}

// 显示注册对话框
const showRegisterDialog = () => {
  if (!apiConnected.value) {
    ElMessage.warning('后端API未连接，无法注册')
    return
  }
  
  nickname.value = ''
  registerDialogVisible.value = true
}

// 取消注册
const cancelRegister = () => {
  registerDialogVisible.value = false
}

// 确认昵称
const confirmNickname = async () => {
  if (!nickname.value) {
    ElMessage.warning('昵称不能为空')
    return
  }
  
  try {
    // 显示加载状态
    const loading = ElMessage({
      message: '正在检查昵称...',
      type: 'info',
      duration: 0
    })
    
    // 检查昵称是否可用
    const data = await get(`/users/check-nickname/${encodeURIComponent(nickname.value)}`)
    
    // 关闭加载提示
    loading.close()
    
    if (data.code === 200) {
      if (data.data === true) {
        // 昵称可用，显示确认对话框
        registerDialogVisible.value = false
        confirmDialogVisible.value = true
      } else {
        // 昵称已被占用
        ElMessage.warning('该昵称已被占用，请尝试其他昵称')
      }
    } else {
      ElMessage.error(data.msg || '检查昵称失败')
    }
  } catch (error: any) {
    console.error('检查昵称错误:', error)
    ElMessage.error(`检查昵称失败: ${error.message}`)
  }
}

// 返回注册对话框
const backToRegister = () => {
  confirmDialogVisible.value = false
  failDialogVisible.value = false
  registerDialogVisible.value = true
}

// 创建用户
const createUser = async () => {
  try {
    // 显示加载状态
    const loading = ElMessage({
      message: '正在创建用户...',
      type: 'info',
      duration: 0
    })
    
    const data = await post('/users', { nickname: nickname.value })
    
    // 关闭加载提示
    loading.close()
    
    confirmDialogVisible.value = false
    
    if (data.code === 200) {
      // 创建成功
      userId.value = data.data.id.toString()
      
      // 保存令牌和用户信息
      tokenUtils.setToken(data.data.token);
      tokenUtils.setUserInfo(data.data.id, data.data.nickname);
      
      successDialogVisible.value = true
    } else if (data.code === 409) {
      // 昵称已被占用
      failDialogVisible.value = true
    } else {
      ElMessage.error(data.msg || '创建用户失败')
    }
  } catch (error: any) {
    console.error('创建用户错误:', error)
    ElMessage.error(`创建用户失败: ${error.message}`)
    confirmDialogVisible.value = false
  }
}

// 进入游戏
const enterGame = () => {
  successDialogVisible.value = false
  router.push('/game')
}

// 显示登录对话框
const showLoginDialog = () => {
  if (!apiConnected.value) {
    ElMessage.warning('后端API未连接，无法登录')
    return
  }
  
  userId.value = ''
  isValidUserId.value = false
  loginDialogVisible.value = true
}

// 取消登录
const cancelLogin = () => {
  loginDialogVisible.value = false
}

// 验证用户ID
const validateUserId = () => {
  isValidUserId.value = /^\d{8}$/.test(userId.value)
}

// 登录
const login = async () => {
  try {
    // 显示加载状态
    const loading = ElMessage({
      message: '正在登录...',
      type: 'info',
      duration: 0
    })
    
    const data = await post('/users/login', { id: parseInt(userId.value) })
    
    // 关闭加载提示
    loading.close()
    
    if (data.code === 200) {
      // 登录成功
      loginDialogVisible.value = false
      
      // 保存令牌和用户信息
      tokenUtils.setToken(data.data.token);
      tokenUtils.setUserInfo(data.data.id, data.data.nickname);
      
      ElMessage.success('登录成功')
      
      // 导航到游戏主界面
      router.push('/game')
    } else if (data.code === 404) {
      ElMessage.error('用户ID不存在')
    } else {
      ElMessage.error(data.msg || '登录失败')
    }
  } catch (error: any) {
    console.error('登录错误:', error)
    ElMessage.error(`登录失败: ${error.message}`)
  }
}

// 直接测试API
const testDirectApi = async () => {
  try {
    directTestResult.value = null;
    
    // 显示加载状态
    const loading = ElMessage({
      message: '正在测试API...',
      type: 'info',
      duration: 0
    });
    
    // 直接使用fetch测试API
    const response = await fetch(`${API_BASE_URL}/health`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      },
      mode: 'cors',
      credentials: 'include'
    });
    
    let data = null;
    let error = null;
    
    try {
      data = await response.json();
    } catch (e: any) {
      error = e.message;
    }
    
    // 关闭加载提示
    loading.close();
    
    directTestResult.value = {
      success: response.ok,
      status: response.status,
      data: data,
      error: error
    };
    
    if (response.ok) {
      ElMessage.success('API测试成功');
    } else {
      ElMessage.error(`API测试失败: ${response.status} ${response.statusText}`);
    }
  } catch (error: any) {
    ElMessage.error(`API测试出错: ${error.message}`);
    directTestResult.value = {
      success: false,
      error: error.message
    };
  }
};

// 切换模拟API
const toggleMockApi = () => {
  const newState = !mockApiEnabled.value
  mockApiEnabled.value = newState
  setMockApiEnabled(newState)
  
  if (newState) {
    apiConnected.value = true
    apiStatus.value = '使用模拟API (后端连接失败)'
    ElMessage.success('已启用模拟API，可以继续使用应用')
  } else {
    // 重新检查API连接
    checkApiConnection()
  }
}
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100vh;
  width: 100%;
}

.title-container {
  width: 100%;
  padding: 60px 0;
  text-align: center;
  background-color: #a8c8e0;
  margin-bottom: 0;
  border-bottom: 1px solid #ddd;
}

.game-title {
  font-size: 5rem;
  font-weight: bold;
  color: #333;
  font-family: 'SimSun', 'STSong', serif;
  letter-spacing: 5px;
  margin: 0;
}

.login-section {
  width: 100%;
  padding: 30px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-title {
  text-align: center;
  margin-bottom: 25px;
  font-size: 1.5rem;
  color: #333;
  font-weight: bold;
  position: relative;
  padding-bottom: 10px;
}

.login-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 50px;
  height: 2px;
  background-color: #4CAF50;
}

.api-status {
  margin-bottom: 20px;
  padding: 15px;
  border-radius: 4px;
  text-align: center;
  font-size: 0.9rem;
  width: 100%;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.status-text {
  font-weight: bold;
  margin-bottom: 5px;
}

.api-details {
  font-size: 0.8rem;
  text-align: left;
  background-color: rgba(255, 255, 255, 0.7);
  padding: 8px 12px;
  border-radius: 4px;
  width: 100%;
}

.api-error-details {
  width: 100%;
  text-align: center;
}

.details-toggle {
  font-size: 0.8rem;
  margin: 5px 0;
}

.detailed-results {
  background-color: rgba(255, 255, 255, 0.7);
  padding: 10px;
  border-radius: 4px;
  text-align: left;
  font-size: 0.8rem;
  margin-top: 5px;
}

.test-result {
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #ddd;
}

.test-result:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.api-connected {
  background-color: #f0f9eb;
  color: #67c23a;
  border: 1px solid #e1f3d8;
}

.api-disconnected {
  background-color: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fde2e2;
}

.api-actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.action-button {
  min-width: 100px;
}

.direct-test-result {
  margin-top: 15px;
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  text-align: left;
  font-size: 0.8rem;
  width: 100%;
  max-height: 200px;
  overflow-y: auto;
}

.direct-test-result pre {
  background-color: #eee;
  padding: 5px;
  border-radius: 3px;
  overflow-x: auto;
  margin: 5px 0;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 100%;
  max-width: 400px;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 1.1rem;
  border-radius: 4px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
}

.dialog-content {
  text-align: center;
  margin: 20px 0;
}

.user-id {
  font-size: 2.5rem;
  font-weight: bold;
  margin: 20px 0;
  color: #2196F3;
  letter-spacing: 2px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
}

/* 自定义对话框样式 */
:deep(.el-dialog) {
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

:deep(.el-dialog__header) {
  background-color: #f5f5f5;
  padding: 15px 20px;
  margin: 0;
  border-bottom: 1px solid #eee;
}

:deep(.el-dialog__title) {
  font-weight: bold;
  color: #333;
}

:deep(.el-dialog__body) {
  padding: 30px 20px;
}

:deep(.el-dialog__footer) {
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

:deep(.el-button) {
  border-radius: 4px;
  padding: 10px 25px;
}

:deep(.el-input__inner) {
  border-radius: 4px;
  padding: 0 15px;
  height: 40px;
}

@media (max-width: 768px) {
  .game-title {
    font-size: 3.5rem;
  }
}
</style> 