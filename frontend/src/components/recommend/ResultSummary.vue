<template>
  <div class="space-y-6">
    <!-- 청약 자격 -->
    <div class="border border-gray-200 rounded-2xl p-6">
      <h3 class="text-base font-semibold text-gray-900 mb-4">청약 자격</h3>
      <div class="flex flex-wrap gap-2">
        <span
          v-for="item in eligibilityItems"
          :key="item.label"
          :class="[
            'inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-medium',
            item.eligible
              ? 'bg-gray-900 text-white'
              : 'bg-gray-100 text-gray-400 line-through',
          ]"
        >
          <span v-if="item.eligible">✓</span>
          {{ item.label }}
        </span>
      </div>
    </div>

    <!-- 가점 예상 점수 -->
    <div class="border border-gray-200 rounded-2xl p-6">
      <div class="flex items-end justify-between mb-3">
        <h3 class="text-base font-semibold text-gray-900">민영주택 청약 가점 (추정)</h3>
        <span class="text-2xl font-bold text-gray-900">
          {{ score.total }}
          <span class="text-sm font-normal text-gray-400">/ {{ score.maxScore }}점</span>
        </span>
      </div>

      <!-- 전체 점수 바 -->
      <div class="w-full h-2 bg-gray-100 rounded-full overflow-hidden mb-4">
        <div
          class="h-full bg-gray-900 rounded-full transition-all duration-700"
          :style="{ width: `${(score.total / score.maxScore) * 100}%` }"
        />
      </div>

      <!-- 항목별 점수 -->
      <div class="space-y-2 text-sm">
        <div class="flex justify-between text-gray-600">
          <span>무주택 기간</span>
          <span class="font-medium text-gray-900">{{ score.noHousePeriodScore }}점 / 32점</span>
        </div>
        <div class="flex justify-between text-gray-600">
          <span>부양가족 수</span>
          <span class="font-medium text-gray-900">{{ score.dependentsScore }}점 / 35점</span>
        </div>
        <div class="flex justify-between text-gray-600">
          <span>청약통장 기간</span>
          <span class="font-medium text-gray-900">{{ score.subscriptionPeriodScore }}점 / 17점</span>
        </div>
      </div>
      <p class="mt-3 text-xs text-gray-400">* 입력하신 정보를 바탕으로 한 추정 점수입니다.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { EligibilityResult, PriorityScore } from '@/types/recommend'

const props = defineProps<{
  eligibility: EligibilityResult
  score: PriorityScore
}>()

const eligibilityItems = computed(() => [
  { label: '국민주택', eligible: props.eligibility.nationalHousing },
  { label: '민영주택', eligible: props.eligibility.privateHousing },
  { label: '신혼부부 특별공급', eligible: props.eligibility.specialNewlywed },
  { label: '생애최초 특별공급', eligible: props.eligibility.specialFirstTime },
  { label: '다자녀 특별공급', eligible: props.eligibility.specialMultiChild },
])
</script>
