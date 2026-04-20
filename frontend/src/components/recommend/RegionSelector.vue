<template>
  <div class="space-y-4">
    <!-- 시도 선택 -->
    <div>
      <label class="block text-sm font-medium text-gray-600 mb-1.5">시 / 도</label>
      <select
        v-model="selectedSido"
        @change="selectedSigungu = ''"
        class="w-full border border-gray-300 rounded-lg px-4 py-3 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
      >
        <option value="">선택해주세요</option>
        <option v-for="r in regions" :key="r.sido" :value="r.sido">{{ r.sido }}</option>
      </select>
    </div>

    <!-- 시군구 선택 -->
    <div>
      <label class="block text-sm font-medium text-gray-600 mb-1.5">시 / 군 / 구</label>
      <select
        v-model="selectedSigungu"
        :disabled="!selectedSido"
        class="w-full border border-gray-300 rounded-lg px-4 py-3 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent disabled:bg-gray-50 disabled:text-gray-400"
      >
        <option value="">{{ selectedSido ? '선택해주세요' : '시/도를 먼저 선택해주세요' }}</option>
        <option v-for="sg in currentSigungu" :key="sg" :value="sg">{{ sg }}</option>
      </select>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { regions } from '@/data/regions'

const selectedSido = ref('')
const selectedSigungu = ref('')

const currentSigungu = computed(
  () => regions.find((r) => r.sido === selectedSido.value)?.sigungu ?? [],
)

// 부모에 선택값 전달
const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

watch(selectedSigungu, (val) => {
  emit('update:modelValue', val)
})
</script>
