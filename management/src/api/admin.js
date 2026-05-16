import request from './request'

// --- Dashboard 妯″潡 ---
export function getAdminStats() { 
  return request({ url: '/admin/dashboard/stats', method: 'get' }) 
}
// 鑾峰彇鍥捐〃鏁版嵁锛屾敮鎸佷紶鍏?{ days: 7 } 鎴?{ days: 30 }
export function getAdminCharts(params) { 
  return request({ url: '/admin/dashboard/charts', method: 'get', params }) 
}

// --- 杞挱鍥炬ā鍧?---
export function getAdminBanners() { return request({ url: '/admin/banners', method: 'get' }) }
export function addBanner(data) { return request({ url: '/admin/banners', method: 'post', data }) }
export function updateBanner(data) { return request({ url: '/admin/banners', method: 'put', data }) }
export function updateBannerStatus(id, isActive) { 
  return request({ url: '/admin/banners/status', method: 'put', data: { id, isActive: isActive ? 1 : 0 } }) 
}
export function deleteBanner(id) { return request({ url: `/admin/banners/${id}`, method: 'delete' }) }

// --- 鍟嗗搧妯″潡 ---
export function getAdminProducts(params) { return request({ url: '/admin/products', method: 'get', params }) }
export function addProduct(data) { return request({ url: '/admin/products', method: 'post', data }) }
export function updateProduct(data) { return request({ url: '/admin/products', method: 'put', data }) }
export function deleteProduct(id) { return request({ url: `/admin/products/${id}`, method: 'delete' }) }

// --- 鐢ㄦ埛妯″潡 ---
export function getAdminUsers(params) { return request({ url: '/admin/users', method: 'get', params }) }
export function addUser(data) { return request({ url: '/admin/users', method: 'post', data }) }
// 鐪熷疄鐨勪綑棰濆厖鍊兼帴鍙?
export function rechargeUser(id, amount) { 
  return request({ url: `/admin/users/${id}/recharge`, method: 'put', data: { amount } }) 
}

// --- 璁㈠崟妯″潡 ---
export function getAdminOrders(params) { return request({ url: '/admin/orders', method: 'get', params }) }
// 澶勭悊璁㈠崟娴佽浆锛堝彂璐?瀹屾垚锛?
export function processOrder(id) { return request({ url: `/admin/orders/${id}/process`, method: 'put' }) }

// --- 鍒嗙被妯″潡 ---
export function addCategory(data) { return request({ url: '/admin/categories', method: 'post', data }) }
export function deleteCategory(id) { return request({ url: `/admin/categories/${id}`, method: 'delete' }) }

// --- AI 鍔╂墜 ---
export function chatWithAi(prompt) { 
  return request({ url: '/admin/ai/chat', method: 'post', data: { prompt } }) 
}
// --- Stores Module ---
export function getStores() { return request({ url: '/admin/stores', method: 'get' }) }
export function addStore(data) { return request({ url: '/admin/stores', method: 'post', data }) }
export function updateStore(data) { return request({ url: '/admin/stores', method: 'put', data }) }
export function deleteStore(id) { return request({ url: /admin/stores/, method: 'delete' }) }

// --- Store Products Module ---
export function getStoreProducts(storeId) { return request({ url: '/admin/store-products', method: 'get', params: { storeId } }) }
export function addStoreProduct(data) { return request({ url: '/admin/store-products', method: 'post', data }) }
export function updateStoreProduct(data) { return request({ url: '/admin/store-products', method: 'put', data }) }
export function deleteStoreProduct(id) { return request({ url: /admin/store-products/, method: 'delete' }) }
