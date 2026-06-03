import { ref } from 'vue'
import { apiClient } from '@/utils/apiClient'
import { useQuasar } from 'quasar'

export function useApi<T>() {
  const $q = useQuasar()
  const data = ref<T[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchList = async (endpoint: string, params?: Record<string, unknown>) => {
    loading.value = true
    error.value = null
    try {
      const response = await apiClient.getAxiosInstance().get<T[]>(endpoint, { params })
      data.value = response.data
    } catch (err: unknown) {
      const axiosError = err as { response?: { data?: { message?: string } } }
      error.value = axiosError.response?.data?.message || 'Error al cargar datos'
      $q.notify({
        type: 'negative',
        message: error.value ?? 'Error al cargar datos',
        position: 'top',
      })
    } finally {
      loading.value = false
    }
  }

  const create = async (endpoint: string, payload: unknown) => {
    try {
      await apiClient.getAxiosInstance().post(endpoint, payload)
      $q.notify({
        type: 'positive',
        message: 'Registro creado exitosamente',
        position: 'top',
      })
      return true
    } catch (err: unknown) {
      const axiosError = err as { response?: { data?: { message?: string } } }
      error.value = axiosError.response?.data?.message || 'Error al crear registro'
      $q.notify({
        type: 'negative',
        message: error.value ?? 'Error al crear registro',
        position: 'top',
      })
      return false
    }
  }

  const update = async (endpoint: string, payload: unknown) => {
    try {
      await apiClient.getAxiosInstance().put(endpoint, payload)
      $q.notify({
        type: 'positive',
        message: 'Registro actualizado exitosamente',
        position: 'top',
      })
      return true
    } catch (err: unknown) {
      const axiosError = err as { response?: { data?: { message?: string } } }
      error.value = axiosError.response?.data?.message || 'Error al actualizar registro'
      $q.notify({
        type: 'negative',
        message: error.value ?? 'Error al actualizar registro',
        position: 'top',
      })
      return false
    }
  }

  const deleteRecord = async (endpoint: string) => {
    try {
      await apiClient.getAxiosInstance().delete(endpoint)
      $q.notify({
        type: 'positive',
        message: 'Registro eliminado exitosamente',
        position: 'top',
      })
      return true
    } catch (err: unknown) {
      const axiosError = err as { response?: { data?: { message?: string } } }
      error.value = axiosError.response?.data?.message || 'Error al eliminar registro'
      $q.notify({
        type: 'negative',
        message: error.value ?? 'Error al eliminar registro',
        position: 'top',
      })
      return false
    }
  }

  return {
    data,
    loading,
    error,
    fetchList,
    create,
    update,
    deleteRecord,
  }
}
