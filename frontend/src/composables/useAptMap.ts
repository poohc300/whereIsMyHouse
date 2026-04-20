import type { Ref } from 'vue'
import type { AptComplexSummary } from '@/types/recommend'

export function useAptMap(containerRef: Ref<HTMLElement | null>) {
  let map: kakao.maps.Map | null = null
  let openInfoWindow: kakao.maps.InfoWindow | null = null
  // 현재 표시된 마커 목록 (clearMarkers 시 일괄 제거)
  const markerList: kakao.maps.Marker[] = []

  /** 카카오 지도를 초기화한다. SDK 로드 완료 후 콜백으로 실행된다. */
  function initMap() {
    kakao.maps.load(() => {
      if (!containerRef.value) return
      map = new kakao.maps.Map(containerRef.value, {
        center: new kakao.maps.LatLng(37.5665, 126.978), // 기본 중심: 서울 시청
        level: 8,
      })
    })
  }

  /** 현재 지도에 그려진 마커와 열려 있는 인포윈도우를 모두 제거한다. */
  function clearMarkers() {
    markerList.forEach((m) => m.setMap(null))
    markerList.length = 0
    openInfoWindow?.close()
    openInfoWindow = null
  }

  /**
   * 단지 목록을 마커로 지도에 표시한다.
   * - 좌표가 없는 단지는 건너뛴다.
   * - 유효한 단지들의 평균 좌표로 지도 중심을 이동한다.
   * - 마커 클릭 시 단지명 + 평균 매매가/전세가 인포윈도우가 열린다.
   */
  function drawMarkers(complexes: AptComplexSummary[]) {
    if (!map) return
    clearMarkers()

    const valid = complexes.filter((c) => c.lat != null && c.lng != null)
    if (valid.length === 0) return

    // 유효 단지들의 평균 좌표로 지도 중심 이동
    const avgLat = valid.reduce((s, c) => s + c.lat!, 0) / valid.length
    const avgLng = valid.reduce((s, c) => s + c.lng!, 0) / valid.length
    map.setCenter(new kakao.maps.LatLng(avgLat, avgLng))
    map.setLevel(6)

    for (const complex of valid) {
      const position = new kakao.maps.LatLng(complex.lat!, complex.lng!)
      const marker = new kakao.maps.Marker({ position, map: map! })
      markerList.push(marker)

      const infoWindow = new kakao.maps.InfoWindow({
        content: buildInfoContent(complex),
        removable: true,
      })

      // 마커 클릭 시 이전 인포윈도우를 닫고 현재 것을 연다
      kakao.maps.event.addListener(marker, 'click', () => {
        openInfoWindow?.close()
        infoWindow.open(map!, marker)
        openInfoWindow = infoWindow
      })
    }
  }

  return { initMap, drawMarkers, clearMarkers }
}

// ─── 헬퍼 함수 ────────────────────────────────────────────────────────────────

function buildInfoContent(c: AptComplexSummary): string {
  return `
    <div style="padding:12px 16px;min-width:210px;font-family:sans-serif;line-height:1.6">
      <div style="font-size:14px;font-weight:600;color:#111;margin-bottom:3px">${c.name}</div>
      <div style="font-size:11px;color:#999;margin-bottom:8px">${c.roadAddr ?? ''}</div>
      <div style="border-top:1px solid #eee;padding-top:8px">
        <div style="font-size:12px;color:#444;margin-bottom:3px">
          매매 평균&nbsp; <b style="color:#111">${formatPrice(c.avgTradePrice)}</b>
        </div>
        <div style="font-size:12px;color:#444">
          전세 평균&nbsp; <b style="color:#111">${formatPrice(c.avgDepositPrice)}</b>
        </div>
      </div>
    </div>
  `
}

/** 만원 단위 숫자를 '억원 / 만원' 형식으로 변환한다. */
function formatPrice(manwon: number | null): string {
  if (manwon == null) return '정보 없음'
  const eok = Math.floor(manwon / 10000)
  const rem = manwon % 10000
  if (eok > 0 && rem > 0) return `${eok}억 ${rem.toLocaleString()}만원`
  if (eok > 0) return `${eok}억원`
  return `${rem.toLocaleString()}만원`
}
