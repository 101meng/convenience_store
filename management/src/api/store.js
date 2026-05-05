import request from './request'

// 获取首页综合数据（轮播、秒杀、新品）
export function getHomeData() {
  return request({ url: '/home/index', method: 'get' })
}

// 获取分类列表
export function getCategories() {
  return request({ url: '/categories', method: 'get' })
}

// 获取商品列表（支持按分类筛选）
export function getProducts(categoryId) {
  return request({ 
    url: '/products', 
    method: 'get', 
    params: { categoryId } 
  })
}