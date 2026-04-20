<template>
  <button
    type="button"
    @click="$emit('select', option.value)"
    :class="[
      'w-full text-left px-5 py-4 rounded-xl border-2 transition-all duration-150',
      selected
        ? 'border-gray-900 bg-gray-900 text-white'
        : 'border-gray-200 bg-white text-gray-800 hover:border-gray-400',
    ]"
  >
    <span class="block font-medium text-base">{{ option.label }}</span>
    <span v-if="option.description" class="block text-sm mt-0.5 opacity-70">
      {{ option.description }}
    </span>
  </button>
</template>

<script setup lang="ts">
defineProps<{
  option: { value: string | number; label: string; description?: string }
  selected: boolean
}>()

defineEmits<{
  (e: 'select', value: string | number): void
}>()
</script>
