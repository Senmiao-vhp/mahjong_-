// API基础URL

export const API_BASE_URL = 'http://localhost:8080'

/**
 * 获取JWT令牌相关函数
 */
export const tokenUtils = {
  // 保存令牌
  setToken: (token: string) => {
    localStorage.setItem('token', token);
  },
  
  // 获取令牌
  getToken: () => {
    return localStorage.getItem('token');
  },
  
  // 移除令牌
  removeToken: () => {
    localStorage.removeItem('token');
  },
  
  // 从JWT令牌解析用户信息
  parseUserFromToken: (token: string) => {
    try {
      // 获取JWT payload部分
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      
      return JSON.parse(jsonPayload);
    } catch (e) {
      console.error('解析JWT令牌失败', e);
      return null;
    }
  },
  
  // 保存用户信息
  setUserInfo: (id: number, nickname: string) => {
    localStorage.setItem('userId', id.toString());
    localStorage.setItem('userNickname', nickname);
  },
  
  // 获取用户信息
  getUserInfo: () => {
    const token = localStorage.getItem('token');
    if (!token) return null;
    
    try {
      const payload = tokenUtils.parseUserFromToken(token);
      if (payload && payload.sub) {
        // 正常情况下，payload.sub应该是用户ID
        return {
          id: payload.sub,
          nickname: payload.nickname || localStorage.getItem('userNickname') || '未知用户'
        };
      }
    } catch (e) {
      console.error('获取用户信息失败', e);
    }
    
    // 回退到本地存储的信息
    const userId = localStorage.getItem('userId');
    const userNickname = localStorage.getItem('userNickname');
    
    if (userId) {
      return {
        id: userId,
        nickname: userNickname || '未知用户'
      };
    }
    
    return null;
  },
  
  // 清除所有认证信息
  clearAuth: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userNickname');
  }
};

/**
 * 获取请求头
 * @returns Headers对象
 */
function getHeaders() {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    // 添加CORS相关头
    'Origin': window.location.origin,
    'Access-Control-Request-Method': 'GET, POST, OPTIONS',
    'Access-Control-Request-Headers': 'Content-Type, Accept, Authorization'
  }
  
  // 添加认证令牌
  const token = tokenUtils.getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  
  return headers
}

/**
 * 发送GET请求
 * @param url 请求URL
 * @param params 查询参数
 * @returns Promise
 */
export async function get(url: string, params?: Record<string, any>) {
  const queryString = params ? `?${new URLSearchParams(params).toString()}` : ''
  // 直接使用原始URL，不再检查/api前缀
  const fullUrl = `${API_BASE_URL}${url}${queryString}`
  
  console.log(`发送GET请求: ${fullUrl}`)
  
  try {
    const headers = getHeaders()
    
    // 添加请求开始时间
    const startTime = new Date().getTime()
    
    // 首先发送一个预检请求
    try {
      await fetch(fullUrl, {
        method: 'OPTIONS',
        headers: {
          'Origin': window.location.origin,
          'Access-Control-Request-Method': 'GET',
          'Access-Control-Request-Headers': 'Content-Type, Accept, Authorization'
        },
        mode: 'cors',
        credentials: 'include'
      })
    } catch (e) {
      // 忽略预检请求的错误，继续发送实际请求
      console.warn('预检请求失败，继续发送实际请求', e)
    }
    
    const response = await fetch(fullUrl, {
      method: 'GET',
      headers,
      // 添加跨域支持
      mode: 'cors',
      // 尝试使用凭证
      credentials: 'include',
      // 添加超时处理
      signal: AbortSignal.timeout(10000) // 10秒超时
    })
    
    // 计算请求耗时
    const endTime = new Date().getTime()
    const duration = endTime - startTime
    
    console.log(`GET请求完成 (${duration}ms): ${fullUrl}`, {
      status: response.status,
      statusText: response.statusText
    })
    
    return await handleResponse(response)
  } catch (error: any) {
    // 检查是否为超时错误
    if (error.name === 'TimeoutError' || error.name === 'AbortError') {
      console.error(`GET请求超时 (${fullUrl})`)
      throw new Error(`请求超时，请检查网络连接或服务器状态`)
    }
    
    console.error(`GET请求错误 (${fullUrl}):`, error)
    throw error
  }
}

/**
 * 发送POST请求
 * @param url 请求URL
 * @param data 请求体数据
 * @returns Promise
 */
export async function post(url: string, data?: any) {
  // 直接使用原始URL，不再检查/api前缀
  const fullUrl = `${API_BASE_URL}${url}`
  
  console.log(`发送POST请求: ${fullUrl}`, data)
  
  try {
    const headers = getHeaders()
    
    // 添加请求开始时间
    const startTime = new Date().getTime()
    
    // 首先发送一个预检请求
    try {
      await fetch(fullUrl, {
        method: 'OPTIONS',
        headers: {
          'Origin': window.location.origin,
          'Access-Control-Request-Method': 'POST',
          'Access-Control-Request-Headers': 'Content-Type, Accept, Authorization'
        },
        mode: 'cors',
        credentials: 'include'
      })
    } catch (e) {
      // 忽略预检请求的错误，继续发送实际请求
      console.warn('预检请求失败，继续发送实际请求', e)
    }
    
    const response = await fetch(fullUrl, {
      method: 'POST',
      headers,
      body: data ? JSON.stringify(data) : undefined,
      // 添加跨域支持
      mode: 'cors',
      // 尝试使用凭证
      credentials: 'include',
      // 添加超时处理
      signal: AbortSignal.timeout(10000) // 10秒超时
    })
    
    // 计算请求耗时
    const endTime = new Date().getTime()
    const duration = endTime - startTime
    
    console.log(`POST请求完成 (${duration}ms): ${fullUrl}`, {
      status: response.status,
      statusText: response.statusText
    })
    
    return await handleResponse(response)
  } catch (error: any) {
    // 检查是否为超时错误
    if (error.name === 'TimeoutError' || error.name === 'AbortError') {
      console.error(`POST请求超时 (${fullUrl})`)
      throw new Error(`请求超时，请检查网络连接或服务器状态`)
    }
    
    console.error(`POST请求错误 (${fullUrl}):`, error)
    throw error
  }
}

/**
 * 处理响应
 * @param response Fetch响应对象
 * @returns Promise
 */
async function handleResponse(response: Response) {
  console.log(`收到响应: ${response.url}`, {
    status: response.status,
    statusText: response.statusText,
    headers: Object.fromEntries(response.headers.entries())
  })
  
  // 尝试解析响应体
  let responseData
  try {
    responseData = await response.json()
    console.log('响应数据:', responseData)
  } catch (error) {
    console.error('解析响应JSON失败:', error)
    throw new Error(`解析响应失败: ${response.status} ${response.statusText}`)
  }
  
  if (!response.ok) {
    // 处理401未授权错误
    if (response.status === 401) {
      // 清除本地存储的认证信息
      tokenUtils.clearAuth();
      
      // 重定向到登录页
      window.location.href = '/'
      
      throw new Error('登录已过期，请重新登录')
    }
    
    // 处理403禁止访问错误
    if (response.status === 403) {
      throw new Error('没有权限执行此操作')
    }
    
    // 处理404未找到错误
    if (response.status === 404) {
      throw new Error('请求的资源不存在')
    }
    
    // 处理500服务器错误
    if (response.status >= 500) {
      throw new Error('服务器内部错误，请稍后再试')
    }
    
    throw new Error(responseData.msg || `请求失败: ${response.status} ${response.statusText}`)
  }
  
  return responseData
}

// 添加一个简单的测试函数，用于检查API连接
export async function testApiConnection() {
  try {
    // 使用健康检查端点
    const response = await fetch(`${API_BASE_URL}/health`, {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
      headers: {
        'Accept': 'application/json',
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        'Pragma': 'no-cache',
        'Expires': '0'
      },
      // 设置较短的超时时间，避免长时间等待
      signal: AbortSignal.timeout(3000) // 3秒超时
    });
    
    console.log('API健康检查结果:', {
      ok: response.ok,
      status: response.status,
      statusText: response.statusText,
      url: response.url
    });
    
    // 尝试解析响应体
    let responseData = null;
    try {
      if (response.ok) {
        responseData = await response.json();
        console.log('健康检查响应数据:', responseData);
      }
    } catch (e: any) {
      console.warn('解析健康检查响应失败:', e);
    }
    
    return {
      ok: response.ok,
      status: response.status,
      statusText: response.statusText,
      data: responseData
    };
  } catch (error: any) {
    console.error('API健康检查失败:', error);
    return {
      ok: false,
      error: error.message || '未知错误'
    };
  }
}

// 添加一个备用的测试函数，用于在主测试函数失败时尝试其他端点
export async function fallbackTestApiConnection() {
  try {
    // 尝试使用游客登录端点作为备用
    const response = await fetch(`${API_BASE_URL}/auth/guest`, {
      method: 'OPTIONS',
      mode: 'cors',
      credentials: 'include',
      headers: {
        'Accept': 'application/json',
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        'Pragma': 'no-cache',
        'Expires': '0'
      },
      signal: AbortSignal.timeout(3000) // 3秒超时
    });
    
    console.log('备用API连接测试结果:', {
      ok: response.ok,
      status: response.status,
      statusText: response.statusText,
      url: response.url
    });
    
    return {
      ok: response.ok,
      status: response.status,
      statusText: response.statusText,
      data: null
    };
  } catch (error: any) {
    console.error('备用API连接测试失败:', error);
    return {
      ok: false,
      error: error.message || '未知错误'
    };
  }
}

// 添加一个全面的API连接测试函数
export async function comprehensiveApiTest() {
  const results = {
    health: null as any,
    guest: null as any,
    checkNickname: null as any,
    overall: false
  };
  
  try {
    // 测试健康检查端点
    results.health = await testApiConnection();
    
    // 如果健康检查成功，则整体测试成功
    if (results.health.ok) {
      results.overall = true;
      return results;
    }
    
    // 测试游客登录端点
    try {
      results.guest = await fallbackTestApiConnection();
      if (results.guest.ok) {
        results.overall = true;
      }
    } catch (e: any) {
      results.guest = { ok: false, error: e.message };
    }
    
    // 测试昵称检查端点
    try {
      const response = await fetch(`${API_BASE_URL}/auth/check-nickname/test`, {
        method: 'GET',
        mode: 'cors',
        credentials: 'include',
        headers: {
          'Accept': 'application/json'
        },
        signal: AbortSignal.timeout(3000)
      });
      
      results.checkNickname = {
        ok: response.ok,
        status: response.status,
        statusText: response.statusText
      };
      
      if (response.ok) {
        results.overall = true;
      }
    } catch (e: any) {
      results.checkNickname = { ok: false, error: e.message };
    }
    
    return results;
  } catch (error: any) {
    console.error('综合API测试失败:', error);
    return {
      ...results,
      overall: false,
      error: error.message
    };
  }
} 