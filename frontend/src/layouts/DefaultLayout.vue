<template>
  <div class="min-h-screen bg-white font-sans flex flex-col">
    <TheNavbar />

    <main class="flex-1">
      <RouterView />
    </main>

    <footer class="border-t border-gray-100 py-8">
      <div class="w-full max-w-screen-xl mx-auto px-4 sm:px-6 lg:px-10 xl:px-16">
        <span class="text-sm text-gray-400">© 2026 Housing</span>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import TheNavbar from '@/components/TheNavbar.vue'
</script>
