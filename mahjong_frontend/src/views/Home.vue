<template>
  <div class="home-container">
    <div class="title-container">
      <h1 class="game-title">雀鬼</h1>
    </div>
    
    <div class="login-section">
      <h2 class="login-title">游客登录</h2>
      
      <!-- 后端连接状态提示 -->
      <div v-if="showConnectionAlert" class="connection-alert">
        <el-alert
          :title="connectionAlertMessage"
          :type="connectionAlertType"
          :closable="true"
          @close="closeConnectionAlert"
          show-icon
        >
          <template #default>
            <p>可尝试点击按钮，错误可能会被自动处理</p>
            <el-button type="text" @click="showHelpDialog">查看连接问题帮助</el-button>
          </template>
        </el-alert>
      </div>
      
      <div class="button-group">
        <el-button type="success" class="login-button" @click="guestLogin">
          游客登录
        </el-button>
        <el-button type="primary" class="login-button" @click="showRegisterDialog">
          注册新ID并登录
        </el-button>
        <el-button type="primary" class="login-button" @click="showLoginDialog">
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

    <!-- 连接问题帮助对话框 -->
    <el-dialog
      v-model="helpDialogVisible"
      title="连接问题帮助"
      width="50%"
      center
    >
      <div class="help-content">
        <h3>后端连接问题解决方案</h3>
        <p>您遇到的问题可能是因为后端服务未启动或者存在跨域资源共享(CORS)问题。</p>
        
        <h4>1. 检查后端服务是否已启动</h4>
        <p>请确保后端服务已经在本地启动，并在端口8080上运行。您可以通过以下步骤检查：</p>
        <ul>
          <li>打开命令行终端</li>
          <li>导航到后端项目目录</li>
          <li>执行启动命令：<code>mvn spring-boot:run</code> 或启动Spring Boot应用</li>
          <li>确认控制台显示服务已在端口8080启动</li>
        </ul>
        
        <h4>2. 检查跨域资源共享(CORS)配置</h4>
        <p>如果后端服务已启动但仍然无法连接，可能是CORS配置问题。您可以：</p>
        <ul>
          <li>检查后端WebConfig.java中的CORS配置</li>
          <li>确保allowedOrigins包含了前端应用的URL (例如 http://localhost:5174)</li>
          <li>确保配置了正确的allowedMethods和allowedHeaders</li>
        </ul>
        
        <h4>3. 检查JWT拦截器配置</h4>
        <p>从错误日志看，可能是JWT拦截器拦截了游客登录请求。可以检查：</p>
        <ul>
          <li>确保/auth/guest路径已在JwtAuthInterceptor的excludePathPatterns中排除</li>
          <li>前端请求中是否正确设置了CORS相关的头信息</li>
          <li>确保OPTIONS预检请求也被正确处理</li>
        </ul>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="helpDialogVisible = false">我知道了</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { get, post, API_BASE_URL, tokenUtils } from '../utils/api'

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
const helpDialogVisible = ref(false)

// 表单数据
const nickname = ref('')
const userId = ref('')
const isValidUserId = ref(false)

// 连接提示相关
const showConnectionAlert = ref(false)
const connectionAlertMessage = ref('连接到服务器失败，请确保后端服务已启动')
const connectionAlertType = ref('warning')

// 关闭连接提示
const closeConnectionAlert = () => {
  showConnectionAlert.value = false
}

// 显示连接错误提示
const showConnectionError = (message?: string) => {
  connectionAlertMessage.value = message || '连接到服务器失败，请确保后端服务已启动'
  connectionAlertType.value = 'error'
  showConnectionAlert.value = true
}

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
    // 先尝试直接检查是否可以访问后端
    const response = await fetch(`${API_BASE_URL}/health`, {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
      headers: {
        'Accept': 'application/json'
      },
      // 设置较短的超时时间
      signal: AbortSignal.timeout(3000) // 3秒超时
    })
    
    // 一个简单的结果对象
    const results: ComprehensiveTestResult = {
      health: {
        ok: response.ok || response.status === 401, // 401也认为是成功
        status: response.status,
        statusText: response.statusText
      },
      guest: null as any,
      checkNickname: null as any,
      overall: response.ok || response.status === 401
    }
    
    apiTestResults.value = results
    
    if (results.overall) {
      // 对于401错误的特殊处理
      if (response.status === 401) {
        apiStatus.value = '后端API连接正常 (JWT认证)'
        apiConnected.value = true
        
        // 设置基本API详情
        apiDetails.value = {
          service: 'Mahjong Game API',
          version: '1.0.0',
          timestamp: new Date().toLocaleString(),
          status: 'PROTECTED'
        }
      } else {
        apiStatus.value = '后端API连接正常'
        apiConnected.value = true
        
        // 如果是200，尝试解析响应数据
        try {
          const responseData = await response.json()
          if (responseData && responseData.data) {
            apiDetails.value = {
              service: responseData.data.service || 'Mahjong Game API',
              version: responseData.data.version || '1.0.0',
              timestamp: responseData.data.timestamp || new Date().toLocaleString(),
              status: responseData.data.status || 'UP'
            }
          }
        } catch (e) {
          console.warn('无法解析健康检查响应数据', e)
          // 默认值
          apiDetails.value = {
            service: 'Mahjong Game API',
            version: '1.0.0',
            timestamp: new Date().toLocaleString(),
            status: 'UP'
          }
        }
      }
    } else {
      apiStatus.value = '后端API连接失败'
      apiConnected.value = false
      
      // 显示连接警告
      showConnectionAlert.value = true
      connectionAlertMessage.value = `后端服务连接问题: HTTP ${response.status} ${response.statusText}`
    }
  } catch (error: any) {
    console.error('API连接检查错误:', error)
    apiStatus.value = `后端API连接错误: ${error.message}`
    apiConnected.value = false
    
    // 显示连接错误
    showConnectionError(error.message)
    
    // 尝试进行额外的诊断
    try {
      // 简单测试游客登录端点
      const fallbackResponse = await fetch(`${API_BASE_URL}/auth/guest`, {
        method: 'GET',
        mode: 'cors',
        credentials: 'include',
        headers: {
          'Accept': 'application/json'
        },
        signal: AbortSignal.timeout(2000) // 2秒超时
      })
      
      if (fallbackResponse.ok || fallbackResponse.status === 401) {
        // 如果这个成功了但主健康检查失败，可能是健康检查端点有问题
        apiConnected.value = true
        apiStatus.value = '后端API可能在线，但健康检查失败'
        // 减轻错误消息的严重性
        connectionAlertType.value = 'warning'
        connectionAlertMessage.value = '后端服务可能已启动，但健康检查失败。您可以尝试登录。'
      }
    } catch (fallbackError) {
      // 忽略后备测试的错误
      console.warn('后备连接测试也失败了', fallbackError)
    }
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
  try {
    // 显示加载状态
    const loading = ElMessage({
      message: '正在游客登录...',
      type: 'info',
      duration: 0
    })
    
    const data = await post('/auth/guest')
    
    // 关闭加载提示
    loading.close()
    
    // 关闭连接警告（如果存在）
    showConnectionAlert.value = false
    
    if (data.code === 200) {
      // 登录成功
      
      // 保存令牌和用户信息
      tokenUtils.setToken(data.data.token);
      tokenUtils.setUserInfo(data.data.id, data.data.nickname);
      
      ElMessage.success(`游客登录成功，您的ID为: ${data.data.id}`)
      
      // 导航到游戏主界面
      router.push('/game')
    } else {
      ElMessage.error(data.msg || '登录失败')
    }
  } catch (error: any) {
    console.error('游客登录错误:', error)
    
    // 处理CORS错误
    if (error.message && (error.message.includes('Failed to fetch') || error.message.includes('NetworkError'))) {
      showConnectionError('连接到服务器失败，请确保后端服务已启动')
    } else if (error.message && error.message.includes('cors')) {
      showConnectionError('跨域请求被拒绝，这可能是服务器CORS配置问题')
    } else {
      ElMessage.error(`登录失败: ${error.message}`)
    }
  }
}

// 显示注册对话框
const showRegisterDialog = () => {
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
    const data = await get(`/auth/check-nickname/${encodeURIComponent(nickname.value)}`)
    
    // 关闭加载提示
    loading.close()
    
    // 成功连接，关闭警告
    showConnectionAlert.value = false
    
    console.log('昵称检查结果:', data)
    
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
    
    // 处理CORS错误
    if (error.message && (error.message.includes('Failed to fetch') || error.message.includes('NetworkError'))) {
      showConnectionError('连接到服务器失败，请确保后端服务已启动')
    } else if (error.message && error.message.includes('cors')) {
      showConnectionError('跨域请求被拒绝，这可能是服务器CORS配置问题')
    } else {
      ElMessage.error(`检查昵称失败: ${error.message}`)
    }
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
    
    const data = await post('/auth/register', { nickname: nickname.value })
    
    // 关闭加载提示
    loading.close()
    
    // 成功连接，关闭警告
    showConnectionAlert.value = false
    
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
    
    // 处理CORS错误
    if (error.message && (error.message.includes('Failed to fetch') || error.message.includes('NetworkError'))) {
      showConnectionError('连接到服务器失败，请确保后端服务已启动')
    } else if (error.message && error.message.includes('cors')) {
      showConnectionError('跨域请求被拒绝，这可能是服务器CORS配置问题')
    } else {
      ElMessage.error(`创建用户失败: ${error.message}`)
    }
    
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
    
    const data = await post('/auth/login', { id: parseInt(userId.value) })
    
    // 关闭加载提示
    loading.close()
    
    // 成功连接，关闭警告
    showConnectionAlert.value = false
    
    if (data.code === 200) {
      // 登录成功
      loginDialogVisible.value = false
      
      // 保存令牌和用户信息
      tokenUtils.setToken(data.data.token);
      tokenUtils.setUserInfo(data.data.id, data.data.nickname);
      
      ElMessage.success(`登录成功，欢迎回来 ${data.data.nickname}`)
      
      // 导航到游戏主界面
      router.push('/game')
    } else if (data.code === 404) {
      ElMessage.error('用户ID不存在')
    } else {
      ElMessage.error(data.msg || '登录失败')
    }
  } catch (error: any) {
    console.error('登录错误:', error)
    
    // 处理CORS错误
    if (error.message && (error.message.includes('Failed to fetch') || error.message.includes('NetworkError'))) {
      showConnectionError('连接到服务器失败，请确保后端服务已启动')
    } else if (error.message && error.message.includes('cors')) {
      showConnectionError('跨域请求被拒绝，这可能是服务器CORS配置问题')
    } else {
      ElMessage.error(`登录失败: ${error.message}`)
    }
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

// 显示连接问题帮助对话框
const showHelpDialog = () => {
  helpDialogVisible.value = true
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

.backend-error {
  margin-top: 10px;
  width: 100%;
  text-align: center;
}

.error-action {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.test-result-status {
  font-size: 1.2rem;
  font-weight: bold;
  margin: 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.test-success {
  color: #67c23a;
}

.test-fail {
  color: #f56c6c;
}

.test-error {
  color: #f56c6c;
  background-color: #fef0f0;
  padding: 5px;
  border-radius: 4px;
  margin-top: 5px;
}

.error-details {
  margin: 10px 0;
  font-size: 0.9rem;
}

.error-actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}

.api-error-details {
  margin-top: 10px;
  text-align: center;
  width: 100%;
}

.connection-alert {
  margin-bottom: 10px;
  width: 100%;
  text-align: center;
}

.help-content {
  text-align: left;
  padding: 20px;
}

.help-content h3 {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 10px;
}

.help-content p {
  margin-bottom: 10px;
}

.help-content ul {
  margin-left: 20px;
  margin-bottom: 10px;
}

.help-content li {
  margin-bottom: 5px;
}
</style>
