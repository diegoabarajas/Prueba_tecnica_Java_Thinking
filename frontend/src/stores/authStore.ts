import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiClient } from '@/utils/apiClient'
import type { AuthResponse } from '@/types/models'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<AuthResponse | null>(null)
  const isAuthenticated = computed(() => !!user.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function login(email: string, password: string) {
    loading.value = true
    error.value = null
    try {
      apiClient.setCredentials(email, password)
      const response = await apiClient.getAxiosInstance().get<AuthResponse>('/api/auth/me')
      user.value = response.data
      sessionStorage.setItem('auth_credentials', btoa(`${email}:${password}`))
      return true
    } catch (err: unknown) {
      const axiosError = err as { response?: { data?: { message?: string } } }
      error.value = axiosError.response?.data?.message || 'Error de autenticación'
      apiClient.clearCredentials()
      return false
    } finally {
      loading.value = false
    }
  }

  function logout() {
    user.value = null
    apiClient.clearCredentials()
    sessionStorage.removeItem('auth_credentials')
  }

  function restoreSession() {
    const credentials = sessionStorage.getItem('auth_credentials')
    if (credentials) {
      const decoded = atob(credentials)
      const colonIndex = decoded.indexOf(':')
      const email = decoded.substring(0, colonIndex)
      const password = decoded.substring(colonIndex + 1)
      return login(email, password)
    }
    return Promise.resolve(false)
  }

  return {
    user,
    isAuthenticated,
    isAdmin,
    loading,
    error,
    login,
    logout,
    restoreSession,
  }
})
