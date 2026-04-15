import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

export function useAuthFetch() {
  const store = useAuthStore()

  function getHeaders(): Record<string, string> {
    const token = localStorage.getItem('auth_token')
    return token ? { Authorization: `Bearer ${token}` } : {}
  }

  async function authFetch<T = any>(
    url: string,
    options: Record<string, any> = {}
  ): Promise<T> {
    try {
      const response = await axios({
        url,
        ...options,
        headers: {
          ...getHeaders(),
          ...(options.headers ?? {}),
        },
      })
      return response.data
    } catch (e: any) {
      const status = e?.response?.status
      if (status === 401 || status === 403) {
        store.clearUser()
        router.push('/login')
        throw e
      }
      throw e
    }
  }

  return { authFetch }
}
