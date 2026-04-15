import { computed, readonly } from 'vue'
import { useAuthStore } from '@/stores/auth'

export function useAuth() {
  const store = useAuthStore()

  const isLoggedIn = computed(() => store.user !== null)

  function setUser(data: { id: number; username: string }, token: string) {
    store.setUser(data, token)
  }

  function clearUser() {
    store.clearUser()
  }

  return {
    user: readonly(computed(() => store.user)),
    isLoggedIn,
    setUser,
    clearUser,
  }
}
