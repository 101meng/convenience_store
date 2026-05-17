import request from './request'

export function sendAdminCode(phone) {
  return request({
    url: '/admin-auth/sendCode',
    method: 'get',
    params: { phone },
    skipAuth: true
  })
}

export function loginAdmin(data) {
  return request({
    url: '/admin-auth/login',
    method: 'post',
    data,
    skipAuth: true
  })
}
