// 导入封装好的axios请求实例
import request from './request'

// ==================== 仪表盘模块 ====================
/**
 * 获取管理员仪表盘统计数据
 * @returns {Promise} 返回统计数据（订单数、用户数、销售额等）
 */
export function getAdminStats() {
  return request({ url: '/admin/dashboard/stats', method: 'get' })
}

/**
 * 获取管理员仪表盘图表数据
 * @param {Object} params - 查询参数（时间范围、类型等）
 * @returns {Promise} 返回图表数据
 */
export function getAdminCharts(params) {
  return request({ url: '/admin/dashboard/charts', method: 'get', params })
}

// ==================== 轮播图管理模块 ====================
/**
 * 获取轮播图列表
 * @returns {Promise} 返回轮播图数据列表
 */
export function getAdminBanners() { 
  return request({ url: '/admin/banners', method: 'get' }) 
}

/**
 * 新增轮播图
 * @param {Object} data - 轮播图信息（图片、排序、状态等）
 * @returns {Promise} 请求结果
 */
export function addBanner(data) { 
  return request({ url: '/admin/banners', method: 'post', data }) 
}

/**
 * 修改轮播图信息
 * @param {Object} data - 轮播图修改信息（包含ID）
 * @returns {Promise} 请求结果
 */
export function updateBanner(data) { 
  return request({ url: '/admin/banners', method: 'put', data }) 
}

/**
 * 修改轮播图上下架状态
 * @param {Number} id - 轮播图ID
 * @param {Boolean} isActive - 状态：true启用/false禁用
 * @returns {Promise} 请求结果
 */
export function updateBannerStatus(id, isActive) {
  return request({ 
    url: '/admin/banners/status', 
    method: 'put', 
    data: { id, isActive: isActive ? 1 : 0 } 
  })
}

/**
 * 删除轮播图
 * @param {Number} id - 轮播图ID
 * @returns {Promise} 请求结果
 */
export function deleteBanner(id) { 
  return request({ url: `/admin/banners/${id}`, method: 'delete' }) 
}

// ==================== 商品管理模块 ====================
/**
 * 分页获取商品列表
 * @param {Object} params - 查询参数（分页、关键词、分类等）
 * @returns {Promise} 返回商品分页数据
 */
export function getAdminProducts(params) { 
  return request({ url: '/admin/products', method: 'get', params }) 
}

/**
 * 新增商品
 * @param {Object} data - 商品信息
 * @returns {Promise} 请求结果
 */
export function addProduct(data) { 
  return request({ url: '/admin/products', method: 'post', data }) 
}

/**
 * 修改商品信息
 * @param {Object} data - 商品修改信息（包含ID）
 * @returns {Promise} 请求结果
 */
export function updateProduct(data) { 
  return request({ url: '/admin/products', method: 'put', data }) 
}

/**
 * 删除商品
 * @param {Number} id - 商品ID
 * @returns {Promise} 请求结果
 */
export function deleteProduct(id) { 
  return request({ url: `/admin/products/${id}`, method: 'delete' }) 
}

// ==================== 用户管理模块 ====================
/**
 * 分页获取用户列表
 * @param {Object} params - 查询参数（分页、关键词等）
 * @returns {Promise} 返回用户分页数据
 */
export function getAdminUsers(params) { 
  return request({ url: '/admin/users', method: 'get', params }) 
}

/**
 * 新增用户
 * @param {Object} data - 用户信息
 * @returns {Promise} 请求结果
 */
export function addUser(data) { 
  return request({ url: '/admin/users', method: 'post', data }) 
}

/**
 * 用户余额充值
 * @param {Number} id - 用户ID
 * @param {Number} amount - 充值金额
 * @returns {Promise} 请求结果
 */
export function rechargeUser(id, amount) {
  return request({ 
    url: `/admin/users/${id}/recharge`, 
    method: 'put', 
    data: { amount } 
  })
}

// ==================== 订单管理模块 ====================
/**
 * 分页获取订单列表
 * @param {Object} params - 查询参数（分页、订单状态等）
 * @returns {Promise} 返回订单分页数据
 */
export function getAdminOrders(params) { 
  return request({ url: '/admin/orders', method: 'get', params }) 
}

/**
 * 处理订单（发货/确认等）
 * @param {Number} id - 订单ID
 * @returns {Promise} 请求结果
 */
export function processOrder(id) { 
  return request({ url: `/admin/orders/${id}/process`, method: 'put' }) 
}

// ==================== 分类管理模块 ====================
/**
 * 新增商品分类
 * @param {Object} data - 分类信息
 * @returns {Promise} 请求结果
 */
export function addCategory(data) { 
  return request({ url: '/admin/categories', method: 'post', data }) 
}

/**
 * 删除商品分类
 * @param {Number} id - 分类ID
 * @returns {Promise} 请求结果
 */
export function deleteCategory(id) { 
  return request({ url: `/admin/categories/${id}`, method: 'delete' }) 
}

// ==================== AI对话模块 ====================
/**
 * AI智能对话
 * @param {String} prompt - 用户提问内容
 * @returns {Promise} AI返回的回答结果
 */
export function chatWithAi(prompt) {
  return request({ url: '/admin/ai/chat', method: 'post', data: { prompt } })
}

// ==================== 店铺管理模块 ====================
/**
 * 获取店铺列表
 * @returns {Promise} 返回店铺数据列表
 */
export function getStores() { 
  return request({ url: '/admin/stores', method: 'get' }) 
}

/**
 * 新增店铺
 * @param {Object} data - 店铺信息
 * @returns {Promise} 请求结果
 */
export function addStore(data) { 
  return request({ url: '/admin/stores', method: 'post', data }) 
}

/**
 * 修改店铺信息
 * @param {Object} data - 店铺修改信息（包含ID）
 * @returns {Promise} 请求结果
 */
export function updateStore(data) { 
  return request({ url: '/admin/stores', method: 'put', data }) 
}

/**
 * 删除店铺
 * @param {Number} id - 店铺ID
 * @returns {Promise} 请求结果
 */
export function deleteStore(id) { 
  return request({ url: `/admin/stores/${id}`, method: 'delete' }) 
}

// ==================== 店铺商品关联管理模块 ====================
/**
 * 根据店铺ID获取关联商品
 * @param {Number} storeId - 店铺ID
 * @returns {Promise} 返回店铺商品列表
 */
export function getStoreProducts(storeId) { 
  return request({ url: '/admin/store-products', method: 'get', params: { storeId } }) 
}

/**
 * 新增店铺商品关联
 * @param {Object} data - 关联信息
 * @returns {Promise} 请求结果
 */
export function addStoreProduct(data) { 
  return request({ url: '/admin/store-products', method: 'post', data }) 
}

/**
 * 修改店铺商品关联
 * @param {Object} data - 关联修改信息（包含ID）
 * @returns {Promise} 请求结果
 */
export function updateStoreProduct(data) { 
  return request({ url: '/admin/store-products', method: 'put', data }) 
}

/**
 * 删除店铺商品关联
 * @param {Number} id - 关联ID
 * @returns {Promise} 请求结果
 */
export function deleteStoreProduct(id) { 
  return request({ url: `/admin/store-products/${id}`, method: 'delete' }) 
}