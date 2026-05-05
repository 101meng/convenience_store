import request from './request'

// --- Dashboard 模块 ---
export function getAdminStats() { 
  return request({ url: '/admin/dashboard/stats', method: 'get' }) 
}
// 获取图表数据，支持传入 { days: 7 } 或 { days: 30 }
export function getAdminCharts(params) { 
  return request({ url: '/admin/dashboard/charts', method: 'get', params }) 
}

// --- 轮播图模块 ---
export function getAdminBanners() { return request({ url: '/admin/banners', method: 'get' }) }
export function addBanner(data) { return request({ url: '/admin/banners', method: 'post', data }) }
export function updateBanner(data) { return request({ url: '/admin/banners', method: 'put', data }) }
export function updateBannerStatus(id, isActive) { 
  return request({ url: '/admin/banners/status', method: 'put', data: { id, isActive: isActive ? 1 : 0 } }) 
}
export function deleteBanner(id) { return request({ url: `/admin/banners/${id}`, method: 'delete' }) }

// --- 商品模块 ---
export function getAdminProducts(params) { return request({ url: '/admin/products', method: 'get', params }) }
export function addProduct(data) { return request({ url: '/admin/products', method: 'post', data }) }
export function updateProduct(data) { return request({ url: '/admin/products', method: 'put', data }) }
export function deleteProduct(id) { return request({ url: `/admin/products/${id}`, method: 'delete' }) }

// --- 用户模块 ---
export function getAdminUsers(params) { return request({ url: '/admin/users', method: 'get', params }) }
export function addUser(data) { return request({ url: '/admin/users', method: 'post', data }) }
// 真实的余额充值接口
export function rechargeUser(id, amount) { 
  return request({ url: `/admin/users/${id}/recharge`, method: 'put', data: { amount } }) 
}

// --- 订单模块 ---
export function getAdminOrders(params) { return request({ url: '/admin/orders', method: 'get', params }) }
// 处理订单流转（发货/完成）
export function processOrder(id) { return request({ url: `/admin/orders/${id}/process`, method: 'put' }) }

// --- 分类模块 ---
export function addCategory(data) { return request({ url: '/admin/categories', method: 'post', data }) }
export function deleteCategory(id) { return request({ url: `/admin/categories/${id}`, method: 'delete' }) }

// --- AI 助手 ---
export function chatWithAi(prompt) { 
  return request({ url: '/admin/ai/chat', method: 'post', data: { prompt } }) 
}