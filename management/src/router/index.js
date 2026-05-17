import { createRouter, createWebHistory } from 'vue-router'
import { loadAdminSession } from '@/utils/adminSession'

const BRAND_ADMIN = 'brand_admin'
const STORE_MANAGER = 'store_manager'

const routes = [
  {
    path: '/login',
    name: 'Login',
    meta: { public: true },
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
        meta: { roles: [BRAND_ADMIN, STORE_MANAGER] },
        component: () => import('../views/dashboard/Dashboard.vue')
      },
      {
        path: 'products',
        name: 'Products',
        meta: { roles: [BRAND_ADMIN, STORE_MANAGER] },
        component: () => import('../views/product/ProductList.vue')
      },
      {
        path: 'categories',
        name: 'Categories',
        meta: { roles: [BRAND_ADMIN, STORE_MANAGER] },
        component: () => import('../views/category/CategoryList.vue')
      },
      {
        path: 'orders',
        name: 'Orders',
        meta: { roles: [BRAND_ADMIN, STORE_MANAGER] },
        component: () => import('../views/order/OrderList.vue')
      },
      {
        path: 'banners',
        name: 'Banners',
        meta: { roles: [BRAND_ADMIN] },
        component: () => import('../views/banner/BannerList.vue')
      },
      {
        path: 'users',
        name: 'Users',
        meta: { roles: [BRAND_ADMIN] },
        component: () => import('../views/user/UserList.vue')
      },
      {
        path: 'stores',
        name: 'Stores',
        meta: { roles: [BRAND_ADMIN, STORE_MANAGER] },
        component: () => import('../views/store/StoreList.vue')
      },
      {
        path: 'stores/:id/products',
        name: 'StoreProducts',
        meta: { roles: [BRAND_ADMIN, STORE_MANAGER] },
        component: () => import('../views/store/StoreProducts.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const session = loadAdminSession()
  const isLoggedIn = Boolean(session.token)

  if (to.meta.public) {
    if (isLoggedIn && to.path === '/login') {
      return '/dashboard'
    }
    return true
  }

  if (!isLoggedIn) {
    return '/login'
  }

  const allowedRoles = to.meta.roles
  if (allowedRoles && !allowedRoles.includes(session.role)) {
    return session.role === STORE_MANAGER ? '/dashboard' : '/dashboard'
  }

  if (to.name === 'StoreProducts' && session.role === STORE_MANAGER) {
    const targetStoreId = Number(to.params.id)
    if (session.storeId && targetStoreId !== Number(session.storeId)) {
      return `/stores/${session.storeId}/products`
    }
  }

  return true
})

export default router
