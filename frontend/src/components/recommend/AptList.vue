<template>
  <div>
    <h3 class="text-base font-semibold text-gray-900 mb-3">예산 범위 내 추천 단지</h3>

    <p v-if="items.length === 0" class="text-sm text-gray-400 py-6 text-center border border-dashed border-gray-200 rounded-xl">
      해당 지역에서 조건에 맞는 단지를 찾지 못했습니다
    </p>

    <div v-else class="space-y-3">
      <div
        v-for="item in items"
        :key="item.id"
        class="border border-gray-200 rounded-xl p-5 hover:border-gray-400 transition-colors"
      >
        <p class="font-medium text-gray-900">{{ item.name }}</p>
        <p class="text-sm text-gray-500 mt-0.5">{{ item.roadAddr }}</p>

        <div class="mt-3 pt-3 border-t border-gray-100 flex flex-wrap gap-4 text-sm">
          <div v-if="item.avgTradePrice">
            <span class="text-xs text-gray-400 block mb-0.5">평균 매매가</span>
            <span class="font-medium text-gray-900">{{ formatPrice(item.avgTradePrice) }}</span>
          </div>
          <div v-if="item.avgDepositPrice">
            <span class="text-xs text-gray-400 block mb-0.5">평균 전세금</span>
            <span class="font-medium text-gray-900">{{ formatPrice(item.avgDepositPrice) }}</span>
          </div>
          <div v-if="!item.avgTradePrice && !item.avgDepositPrice">
            <span class="text-xs text-gray-400">최근 거래 데이터 없음</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { AptComplexSummary } from '@/types/recommend'

defineProps<{
  items: AptComplexSummary[]
}>()

function formatPrice(manwon: number): string {
  if (manwon >= 10000) {
    const eok = Math.floor(manwon / 10000)
    const rem = manwon % 10000
    return rem > 0 ? `${eok}억 ${rem.toLocaleString()}만원` : `${eok}억원`
  }
  return `${manwon.toLocaleString()}만원`
}
</script>
