import { defineStore } from 'pinia'
import { ref } from 'vue'

interface AuthUser {
  id: number
  username: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<AuthUser | null>(null)

  // 앱 시작 시 localStorage에서 복원
  const stored = localStorage.getItem('auth_user')
  if (stored) {
    try {
      user.value = JSON.parse(stored)
    } catch {
      localStorage.removeItem('auth_user')
    }
  }

  function setUser(data: AuthUser, token: string) {
    user.value = data
    localStorage.setItem('auth_user', JSON.stringify(data))
    localStorage.setItem('auth_token', token)
  }

  function clearUser() {
    user.value = null
    localStorage.removeItem('auth_user')
    localStorage.removeItem('auth_token')
  }

  return { user, setUser, clearUser }
})
