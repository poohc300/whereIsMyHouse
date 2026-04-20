<template>
  <div>
    <h3 class="text-base font-semibold text-gray-900 mb-3">현재 청약 접수 중</h3>

    <p v-if="items.length === 0" class="text-sm text-gray-400 py-6 text-center border border-dashed border-gray-200 rounded-xl">
      자격에 맞는 청약 공고가 없습니다
    </p>

    <div v-else class="space-y-3">
      <div
        v-for="item in items"
        :key="item.id"
        class="border border-gray-200 rounded-xl p-5 hover:border-gray-400 transition-colors"
      >
        <div class="flex items-start justify-between gap-3">
          <div>
            <div class="flex items-center gap-2 mb-1">
              <span class="text-xs font-medium px-2 py-0.5 rounded-full bg-gray-100 text-gray-600">
                {{ item.houseType }}
              </span>
              <span v-if="item.totalSupply" class="text-xs text-gray-400">
                총 {{ item.totalSupply.toLocaleString() }}세대
              </span>
            </div>
            <p class="font-medium text-gray-900">{{ item.houseName }}</p>
            <p class="text-sm text-gray-500 mt-0.5">{{ item.supplyAddr }}</p>
          </div>
        </div>

        <div class="mt-3 pt-3 border-t border-gray-100 flex flex-wrap gap-x-4 gap-y-1 text-xs text-gray-500">
          <span>접수 {{ item.receptionStartDt }} ~ {{ item.receptionEndDt }}</span>
          <span v-if="item.winnerAnnounceDt">당첨 발표 {{ item.winnerAnnounceDt }}</span>
          <span v-if="item.minPrice && item.maxPrice">
            분양가 {{ item.minPrice.toLocaleString() }} ~ {{ item.maxPrice.toLocaleString() }}만원
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { SubscriptionAnnouncement } from '@/types/recommend'

defineProps<{
  items: SubscriptionAnnouncement[]
}>()
</script>
