// 综合API测试工具
// 用于测试API连接状态

import { API_BASE_URL } from './api';

/**
 * 检查后端API健康状态
 */
export async function testApiHealth() {
  try {
    // 使用健康检查端点
    const response = await fetch(`${API_BASE_URL}/health`, {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
      headers: {
        'Accept': 'application/json'
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
    
    // 特殊处理401错误，这可能是JWT拦截器造成的，而不是真正的连接问题
    const is401Error = response.status === 401;
    
    // 尝试解析响应体
    let responseData = null;
    try {
      responseData = await response.json();
      console.log('健康检查响应数据:', responseData);
    } catch (e: any) {
      console.warn('解析健康检查响应失败:', e);
    }
    
    // 如果是401错误但能获取到响应，我们认为服务器是在线的
    // 这可能是JWT拦截器的问题，而不是真正的连接问题
    if (is401Error && responseData) {
      console.log('检测到401错误但服务器在线，可能是JWT拦截器问题');
      return {
        ok: true, // 将其视为连接成功
        status: 401,
        statusText: response.statusText,
        data: responseData,
        note: '检测到后端服务在线，但JWT要求认证。这可能是正常的，不影响应用使用。'
      };
    }
    
    return {
      ok: response.ok || is401Error, // 如果是401，我们也认为API是在线的
      status: response.status,
      statusText: response.statusText,
      data: responseData
    };
  } catch (error: any) {
    console.error('API健康检查失败:', error);
    
    // 区分不同类型的错误
    let errorMsg = '未知错误';
    if (error.name === 'AbortError') {
      errorMsg = '连接超时';
    } else if (error.name === 'TypeError' && error.message.includes('NetworkError')) {
      errorMsg = '网络连接错误';
    } else if (error.message) {
      errorMsg = error.message;
    }
    
    return {
      ok: false,
      error: errorMsg,
      details: error.toString()
    };
  }
}

/**
 * 尝试使用游客登录端点作为备用测试
 */
export async function fallbackTestApiConnection() {
  try {
    // 尝试使用游客登录端点作为备用
    const response = await fetch(`${API_BASE_URL}/auth/guest`, {
      method: 'OPTIONS',
      mode: 'cors',
      credentials: 'include',
      headers: {
        'Accept': 'application/json'
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

/**
 * 全面测试API连接状态
 */
export async function runComprehensiveTest() {
  const results = {
    health: null as any,
    guest: null as any,
    checkNickname: null as any,
    overall: false
  };
  
  try {
    // 测试健康检查端点
    results.health = await testApiHealth();
    
    // 如果健康检查成功，则整体测试成功
    // 注意：即使收到401，我们也认为API是连接的，因为服务器确实响应了
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