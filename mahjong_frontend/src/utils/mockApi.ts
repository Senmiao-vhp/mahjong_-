// 模拟API服务
// 用于在后端不可用时提供测试数据

// 定义模拟API函数类型
export type MockApiFunction = (...args: any[]) => any;

// 定义模拟API函数映射类型
export interface MockApiFunctionMap {
  [key: string]: MockApiFunction;
}

// 模拟用户数据
const mockUsers = [
  { id: 10000001, nickname: '游客10000001', isGuest: true },
  { id: 10000002, nickname: '游客10000002', isGuest: true },
  { id: 10000003, nickname: '测试用户', isGuest: false }
];

// 模拟昵称列表
const mockNicknames = mockUsers.map(user => user.nickname);

// 生成模拟JWT令牌
function generateMockToken(userId: number): string {
  return `mock_token_${userId}_${Date.now()}`;
}

// 模拟健康检查
export function mockHealthCheck() {
  return {
    code: 200,
    msg: '成功',
    data: {
      status: 'UP',
      timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19),
      service: 'Mahjong Game API (Mock)',
      version: '1.0.0-mock'
    }
  };
}

// 模拟游客登录
export function mockGuestLogin() {
  const id = 10000000 + Math.floor(Math.random() * 1000);
  const nickname = `游客${id}`;
  
  return {
    code: 200,
    msg: '成功',
    data: {
      id,
      nickname,
      token: generateMockToken(id)
    }
  };
}

// 模拟检查昵称是否可用
export function mockCheckNickname(nickname: string) {
  const isAvailable = !mockNicknames.includes(nickname);
  
  return {
    code: 200,
    msg: '成功',
    data: isAvailable
  };
}

// 模拟注册用户
export function mockRegisterUser(nickname: string) {
  // 检查昵称是否可用
  if (!mockCheckNickname(nickname).data) {
    return {
      code: 409,
      msg: '昵称已被占用',
      data: null
    };
  }
  
  const id = 10000000 + Math.floor(Math.random() * 1000);
  
  return {
    code: 200,
    msg: '成功',
    data: {
      id,
      nickname,
      token: generateMockToken(id)
    }
  };
}

// 模拟用户登录
export function mockUserLogin(id: number) {
  const user = mockUsers.find(u => u.id === id);
  
  if (!user) {
    return {
      code: 404,
      msg: '用户不存在',
      data: null
    };
  }
  
  return {
    code: 200,
    msg: '成功',
    data: {
      id: user.id,
      nickname: user.nickname,
      token: generateMockToken(user.id)
    }
  };
}

// 模拟获取用户信息
export function mockGetUserInfo(id: number) {
  const user = mockUsers.find(u => u.id === id);
  
  if (!user) {
    return {
      code: 404,
      msg: '用户不存在',
      data: null
    };
  }
  
  return {
    code: 200,
    msg: '成功',
    data: {
      id: user.id,
      nickname: user.nickname
    }
  };
}

// 是否启用模拟API
export let ENABLE_MOCK_API = false;

// 设置模拟API开关
export function setMockApiEnabled(enabled: boolean) {
  ENABLE_MOCK_API = enabled;
  console.log(`模拟API已${enabled ? '启用' : '禁用'}`);
  return ENABLE_MOCK_API;
}

// 导出模拟API函数映射
export const mockApiFunctions: MockApiFunctionMap = {
  '/health': mockHealthCheck,
  '/users/guest': mockGuestLogin,
  '/users/check-nickname': mockCheckNickname,
  '/users': mockRegisterUser,
  '/users/login': mockUserLogin
}; 