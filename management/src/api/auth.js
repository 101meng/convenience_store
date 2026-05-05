// src/api/auth.js
import request from './request'

// 发送验证码
export function sendCode(phone) {
  return request({ 
    url: '/auth/sendCode', 
    method: 'get', 
    params: { phone } 
  })
}

// 登录接口[cite: 17]
export function login(data) {
  return request({ 
    url: '/auth/login', 
    method: 'post', 
    data 
  })
}