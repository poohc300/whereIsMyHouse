import { ref } from 'vue'
import axios from 'axios'
import { useApiBase } from './useApiBase'
import type { RecommendRequest, RecommendResponse } from '@/types/recommend'

export function useRecommend() {
  const apiBase = useApiBase()
  const result = ref<RecommendResponse | null>(null)
  const loading = ref(false)
  const error = ref('')

  async function fetchRecommend(request: RecommendRequest) {
    loading.value = true
    error.value = ''
    result.value = null
    try {
      const { data } = await axios.post<RecommendResponse>(`${apiBase}/api/recommend`, request)
      result.value = data
    } catch (e: any) {
      error.value = e?.response?.data?.message ?? '추천을 불러오는 데 실패했습니다.'
    } finally {
      loading.value = false
    }
  }

  return { result, loading, error, fetchRecommend }
}
