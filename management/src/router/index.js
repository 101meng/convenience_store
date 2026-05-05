import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../layout/index.vue'),
    redirect: '/dashboard',
    children: [
      { 
        path: 'dashboard', 
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue') 
      },
      { 
        path: 'products', 
        name: 'Products',
        component: () => import('../views/product/ProductList.vue') 
      },
      { 
        path: 'categories', 
        name: 'Categories',
        component: () => import('../views/category/CategoryList.vue') 
      },
      { 
        path: 'orders', 
        name: 'Orders',
        component: () => import('../views/order/OrderList.vue') 
      },
      { 
        path: 'banners', 
        name: 'Banners',
        component: () => import('../views/banner/BannerList.vue') 
      },
      { 
        path: 'users', 
        name: 'Users',
        component: () => import('../views/user/UserList.vue') 
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router