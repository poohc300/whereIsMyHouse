<template>
  <header class="border-b border-gray-200 sticky top-0 bg-white z-10">
    <div class="w-full max-w-screen-xl mx-auto px-4 sm:px-6 lg:px-10 xl:px-16 py-4 flex items-center justify-between">
      <RouterLink to="/" class="text-xl sm:text-2xl font-bold tracking-tight text-black">
        Housing
      </RouterLink>
      <nav class="flex items-center gap-4 sm:gap-6 text-sm text-gray-600">
        <RouterLink to="/" class="hover:text-black transition-colors">홈</RouterLink>
        <RouterLink to="/recommend" class="hover:text-black transition-colors">주택 추천</RouterLink>
        <template v-if="isLoggedIn">
          <span class="text-gray-400">{{ user?.username }}</span>
          <button @click="logout" class="hover:text-black transition-colors">로그아웃</button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="hover:text-black transition-colors">로그인</RouterLink>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup lang="ts">
import { useAuth } from '@/composables/useAuth'
import { useRouter } from 'vue-router'

const { user, isLoggedIn, clearUser } = useAuth()
const router = useRouter()

function logout() {
  clearUser()
  router.push('/login')
}
</script>
