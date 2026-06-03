import axios, { AxiosInstance, InternalAxiosRequestConfig } from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const API_TIMEOUT = parseInt(import.meta.env.VITE_API_TIMEOUT || '30000')

class ApiClient {
  private client: AxiosInstance
  private credentials: { email: string; password: string } | null = null

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: API_TIMEOUT,
      headers: {
        'Content-Type': 'application/json',
      },
    })

    this.client.interceptors.request.use((config: InternalAxiosRequestConfig) => {
      if (this.credentials) {
        const encoded = btoa(`${this.credentials.email}:${this.credentials.password}`)
        config.headers.Authorization = `Basic ${encoded}`
      }
      return config
    })

    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          this.credentials = null
          window.location.href = '/login'
        }
        return Promise.reject(error)
      }
    )
  }

  setCredentials(email: string, password: string) {
    this.credentials = { email, password }
  }

  clearCredentials() {
    this.credentials = null
  }

  getAxiosInstance(): AxiosInstance {
    return this.client
  }
}

export const apiClient = new ApiClient()
