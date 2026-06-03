import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/DashboardView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/empresas',
    name: 'Empresas',
    component: () => import('@/views/EmpresasView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/productos',
    name: 'Productos',
    component: () => import('@/views/ProductosView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/categorias',
    name: 'Categorias',
    component: () => import('@/views/CategoriasView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/clientes',
    name: 'Clientes',
    component: () => import('@/views/ClientesView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/ordenes',
    name: 'Ordenes',
    component: () => import('@/views/OrdenesView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/inventario',
    name: 'Inventario',
    component: () => import('@/views/InventarioView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to, _from, next) => {
  const authStore = useAuthStore()

  if (!authStore.isAuthenticated && to.meta.requiresAuth) {
    await authStore.restoreSession()
  }

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
