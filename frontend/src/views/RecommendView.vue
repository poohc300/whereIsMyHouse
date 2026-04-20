<template>
  <div class="w-full max-w-screen-xl mx-auto px-4 sm:px-6 lg:px-10 xl:px-16 py-12">

    <!-- ───────────────────── 위저드 ───────────────────── -->
    <template v-if="!result">
      <!-- 진행 바 -->
      <div class="mb-8">
        <div class="flex items-center justify-between text-xs text-gray-400 mb-2">
          <span>{{ currentStep + 1 }} / {{ totalSteps }}</span>
          <button
            v-if="currentStep > 0"
            @click="currentStep--"
            class="hover:text-gray-700 transition-colors"
          >
            ← 이전
          </button>
        </div>
        <div class="w-full h-1 bg-gray-100 rounded-full">
          <div
            class="h-full bg-gray-900 rounded-full transition-all duration-300"
            :style="{ width: `${((currentStep + 1) / totalSteps) * 100}%` }"
          />
        </div>
      </div>

      <!-- 카드 선택 스텝 (1~7) -->
      <template v-if="currentStep < cardSteps.length">
        <div class="max-w-lg">
          <h2 class="text-2xl font-bold text-gray-900 mb-1">{{ cardSteps[currentStep].title }}</h2>
          <p v-if="cardSteps[currentStep].description" class="text-sm text-gray-500 mb-6">
            {{ cardSteps[currentStep].description }}
          </p>
          <p v-else class="mb-6" />

          <div class="space-y-2.5">
            <StepCard
              v-for="opt in cardSteps[currentStep].options"
              :key="String(opt.value)"
              :option="opt"
              :selected="answers[cardSteps[currentStep].field] === opt.value"
              @select="handleCardSelect(cardSteps[currentStep].field, $event)"
            />
          </div>
        </div>
      </template>

      <!-- 지역 선택 스텝 (8번째) -->
      <template v-else>
        <div class="max-w-lg">
          <h2 class="text-2xl font-bold text-gray-900 mb-1">선호하는 지역이 어디인가요?</h2>
          <p class="text-sm text-gray-500 mb-6">추천 단지와 청약 공고를 해당 지역 기준으로 보여드립니다</p>

          <RegionSelector v-model="answers.preferredSigungu" />

          <button
            @click="submit"
            :disabled="!answers.preferredSigungu || loading"
            class="mt-6 w-full bg-gray-900 text-white py-3.5 rounded-xl font-medium text-sm hover:bg-gray-700 transition-colors disabled:opacity-40"
          >
            {{ loading ? '분석 중...' : '결과 보기' }}
          </button>

          <p v-if="error" class="mt-3 text-sm text-red-500 text-center">{{ error }}</p>
        </div>
      </template>
    </template>

    <!-- ───────────────────── 결과 ───────────────────── -->
    <template v-else>
      <div class="flex items-center justify-between mb-8">
        <h2 class="text-2xl font-bold text-gray-900">맞춤 주택 추천 결과</h2>
        <button
          @click="reset"
          class="text-sm text-gray-500 hover:text-gray-900 transition-colors border border-gray-200 px-4 py-2 rounded-lg"
        >
          다시 하기
        </button>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <ResultSummary :eligibility="result.eligibility" :score="result.priorityScore" />
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <SubscriptionList :items="result.activeSubscriptions" />
        <AptList :items="result.aptRecommendations" />
      </div>
    </template>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import StepCard from '@/components/recommend/StepCard.vue'
import RegionSelector from '@/components/recommend/RegionSelector.vue'
import ResultSummary from '@/components/recommend/ResultSummary.vue'
import SubscriptionList from '@/components/recommend/SubscriptionList.vue'
import AptList from '@/components/recommend/AptList.vue'
import { useRecommend } from '@/composables/useRecommend'
import type { RecommendRequest } from '@/types/recommend'

const { result, loading, error, fetchRecommend } = useRecommend()

// ── 스텝 정의 ──────────────────────────────────────────
const cardSteps = [
  {
    title: '연령대가 어떻게 되세요?',
    field: 'ageGroup' as keyof RecommendRequest,
    options: [
      { value: 'TWENTIES', label: '20대' },
      { value: 'THIRTIES', label: '30대' },
      { value: 'FORTIES', label: '40대' },
      { value: 'OVER_FIFTIES', label: '50대 이상' },
    ],
  },
  {
    title: '혼인 상태를 선택해주세요',
    field: 'maritalStatus' as keyof RecommendRequest,
    options: [
      { value: 'SINGLE', label: '미혼' },
      { value: 'MARRIED_WITHIN_7Y', label: '기혼 (결혼 7년 이내)', description: '신혼부부 특별공급 대상' },
      { value: 'MARRIED_OVER_7Y', label: '기혼 (결혼 7년 초과)' },
    ],
  },
  {
    title: '자녀가 몇 명인가요?',
    field: 'childCount' as keyof RecommendRequest,
    options: [
      { value: 0, label: '없음' },
      { value: 1, label: '1명' },
      { value: 2, label: '2명' },
      { value: 3, label: '3명 이상', description: '다자녀 특별공급 대상' },
    ],
  },
  {
    title: '주택을 보유하고 계신가요?',
    field: 'houseOwnership' as keyof RecommendRequest,
    options: [
      { value: 'NONE', label: '무주택', description: '현재 소유한 주택 없음' },
      { value: 'ONE', label: '1주택' },
      { value: 'TWO_OR_MORE', label: '2주택 이상' },
    ],
  },
  {
    title: '연간 소득이 어느 정도인가요?',
    field: 'annualIncomeRange' as keyof RecommendRequest,
    options: [
      { value: 'UNDER_30M', label: '3천만원 미만' },
      { value: 'RANGE_30M_50M', label: '3천 ~ 5천만원' },
      { value: 'RANGE_50M_70M', label: '5천 ~ 7천만원' },
      { value: 'RANGE_70M_100M', label: '7천만원 ~ 1억' },
      { value: 'OVER_100M', label: '1억원 초과' },
    ],
  },
  {
    title: '총 자산이 어느 정도인가요?',
    description: '부동산, 금융자산, 자동차 등 전체를 합산한 금액',
    field: 'totalAssetsRange' as keyof RecommendRequest,
    options: [
      { value: 'UNDER_100M', label: '1억 미만' },
      { value: 'RANGE_100M_300M', label: '1억 ~ 3억' },
      { value: 'RANGE_300M_500M', label: '3억 ~ 5억' },
      { value: 'OVER_500M', label: '5억 초과' },
    ],
  },
  {
    title: '청약통장 가입 기간이 얼마나 됐나요?',
    field: 'subscriptionPeriod' as keyof RecommendRequest,
    options: [
      { value: 'NONE', label: '없음' },
      { value: 'UNDER_1Y', label: '1년 미만' },
      { value: 'RANGE_1Y_2Y', label: '1년 ~ 2년' },
      { value: 'RANGE_2Y_5Y', label: '2년 ~ 5년' },
      { value: 'RANGE_5Y_10Y', label: '5년 ~ 10년' },
      { value: 'OVER_10Y', label: '10년 이상' },
    ],
  },
]

const totalSteps = cardSteps.length + 1 // 카드 스텝 7개 + 지역 선택 1개
const currentStep = ref(0)

const answers = reactive<Partial<RecommendRequest>>({
  preferredSigungu: '',
})

// ── 카드 선택 시 자동으로 다음 스텝 이동 ──────────────
function handleCardSelect(field: keyof RecommendRequest, value: string | number) {
  ;(answers as any)[field] = value
  setTimeout(() => {
    currentStep.value++
  }, 180) // 선택 피드백 후 이동
}

// ── 제출 ──────────────────────────────────────────────
async function submit() {
  await fetchRecommend(answers as RecommendRequest)
}

// ── 초기화 ────────────────────────────────────────────
function reset() {
  currentStep.value = 0
  Object.keys(answers).forEach((k) => delete (answers as any)[k])
  answers.preferredSigungu = ''
  result.value = null
}
</script>
