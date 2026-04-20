<template>
  <div class="flex h-[calc(100vh-64px)]">

    <!-- ── 왼쪽 컨트롤 패널 ──────────────────────────────────── -->
    <aside class="w-72 flex-shrink-0 border-r border-gray-200 bg-white flex flex-col overflow-y-auto">
      <div class="p-6 space-y-4">
        <h2 class="text-base font-bold text-gray-900">지역별 실거래가 조회</h2>

        <!-- 시 · 도 -->
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1.5">시 · 도</label>
          <select
            v-model="selectedSido"
            @change="onSidoChange"
            class="w-full border border-gray-300 rounded-lg px-3 py-2.5 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-gray-900"
          >
            <option value="">선택해주세요</option>
            <option v-for="r in regions" :key="r.sido" :value="r.sido">{{ r.sido }}</option>
          </select>
        </div>

        <!-- 시 · 군 · 구 -->
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1.5">시 · 군 · 구</label>
          <select
            v-model="selectedSigungu"
            @change="onSigunguChange"
            :disabled="!selectedSido"
            class="w-full border border-gray-300 rounded-lg px-3 py-2.5 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-gray-900 disabled:bg-gray-50 disabled:text-gray-400"
          >
            <option value="">{{ selectedSido ? '선택해주세요' : '시/도를 먼저 선택하세요' }}</option>
            <option v-for="sg in currentSigungu" :key="sg" :value="sg">{{ sg }}</option>
          </select>
        </div>

        <!-- 읍 · 면 · 동 -->
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-1.5">읍 · 면 · 동</label>
          <select
            v-model="selectedDong"
            :disabled="!selectedSigungu || dongLoading"
            class="w-full border border-gray-300 rounded-lg px-3 py-2.5 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-gray-900 disabled:bg-gray-50 disabled:text-gray-400"
          >
            <option value="">{{ dongPlaceholder }}</option>
            <option v-for="d in dongs" :key="d" :value="d">{{ d }}</option>
          </select>
        </div>

        <!-- 조회 버튼 -->
        <button
          @click="search"
          :disabled="!selectedSigungu || loading"
          class="w-full bg-gray-900 text-white rounded-lg py-2.5 text-sm font-medium hover:bg-gray-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
        >
          {{ loading ? '조회 중...' : '조회하기' }}
        </button>

        <!-- 결과 요약 -->
        <p v-if="totalCount > 0" class="text-sm text-gray-500 leading-relaxed">
          <span class="font-semibold text-gray-900">{{ totalCount }}개</span> 단지 조회됨
          <br>
          <span class="text-xs">마커를 클릭하면 상세 정보를 볼 수 있습니다.</span>
        </p>
        <p v-if="totalCount === 0 && hasSearched && !loading" class="text-sm text-gray-400">
          해당 지역의 실거래 데이터가 없습니다.
        </p>
        <p v-if="error" class="text-sm text-red-500">{{ error }}</p>
      </div>
    </aside>

    <!-- ── 카카오 지도 ──────────────────────────────────────────── -->
    <div class="flex-1 relative overflow-hidden">
      <div ref="mapContainer" class="w-full h-full" />

      <!-- 초기 안내 오버레이 (아직 조회 전) -->
      <Transition name="fade">
        <div
          v-if="!hasSearched"
          class="absolute inset-0 flex items-center justify-center bg-gray-50 pointer-events-none"
        >
          <div class="text-center px-6">
            <div class="text-5xl mb-4">🗺</div>
            <p class="text-gray-500 text-sm leading-relaxed">
              왼쪽에서 지역을 선택하고<br>
              <span class="font-medium text-gray-700">조회하기</span>를 누르면<br>
              아파트 실거래 정보가 지도에 표시됩니다.
            </p>
          </div>
        </div>
      </Transition>
    </div>

  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import axios from 'axios'
import { regions } from '@/data/regions'
import { useApiBase } from '@/composables/useApiBase'
import { useAptMap } from '@/composables/useAptMap'
import type { AptComplexSummary } from '@/types/recommend'

const apiBase = useApiBase()

// ── 선택 상태 ──────────────────────────────────────────────────
const selectedSido    = ref('')
const selectedSigungu = ref('')
const selectedDong    = ref('')

// ── 동 목록 ────────────────────────────────────────────────────
const dongs       = ref<string[]>([])
const dongLoading = ref(false)

const dongPlaceholder = computed(() => {
  if (!selectedSigungu.value) return '시/군/구를 먼저 선택하세요'
  if (dongLoading.value)       return '불러오는 중...'
  return '전체 (선택 없음)'
})

// ── 지도 ───────────────────────────────────────────────────────
const mapContainer = ref<HTMLElement | null>(null)
const { initMap, drawMarkers, clearMarkers } = useAptMap(mapContainer)

// ── 조회 상태 ──────────────────────────────────────────────────
const hasSearched = ref(false)
const loading     = ref(false)
const error       = ref('')
const totalCount  = ref(0)

// ── 시군구 목록 ────────────────────────────────────────────────
const currentSigungu = computed(
  () => regions.find((r) => r.sido === selectedSido.value)?.sigungu ?? [],
)

// ── 이벤트 핸들러 ──────────────────────────────────────────────

function onSidoChange() {
  selectedSigungu.value = ''
  selectedDong.value    = ''
  dongs.value           = []
  clearMarkers()
  totalCount.value = 0
  hasSearched.value = false
}

async function onSigunguChange() {
  selectedDong.value = ''
  dongs.value        = []
  clearMarkers()
  totalCount.value  = 0
  hasSearched.value = false

  if (!selectedSigungu.value) return

  // 선택된 시군구의 동 목록을 가져온다
  dongLoading.value = true
  try {
    const { data } = await axios.get<string[]>(`${apiBase}/api/apt/dongs`, {
      params: { sigungu: selectedSigungu.value },
    })
    dongs.value = data
  } catch {
    // 동 목록 로드 실패는 전체 조회가 여전히 가능하므로 조용히 무시
  } finally {
    dongLoading.value = false
  }
}

async function search() {
  if (!selectedSigungu.value) return

  loading.value = true
  error.value   = ''
  clearMarkers()

  try {
    const { data } = await axios.get<AptComplexSummary[]>(`${apiBase}/api/apt/complexes/area`, {
      params: {
        sigungu: selectedSigungu.value,
        ...(selectedDong.value ? { dong: selectedDong.value } : {}),
      },
    })

    hasSearched.value = true
    totalCount.value  = data.length
    drawMarkers(data)
  } catch {
    error.value = '실거래 정보를 불러오는 데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// ── 지도 초기화 ────────────────────────────────────────────────
onMounted(() => {
  initMap()
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
