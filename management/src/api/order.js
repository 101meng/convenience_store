import request from './request'

export function getOrderList(userId = 1) {
  return request({ 
    url: '/order/list', 
    method: 'get', 
    params: { userId } 
  })
}