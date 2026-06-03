import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'

export function useAuth() {
  const authStore = useAuthStore()
  const router = useRouter()

  const requireAdmin = () => {
    if (!authStore.isAdmin) {
      router.push('/dashboard')
      throw new Error('Acceso denegado: requiere permisos de administrador')
    }
  }

  return {
    ...authStore,
    requireAdmin,
  }
}
