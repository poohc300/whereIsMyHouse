import type { Ref } from 'vue'
import type { AptComplexSummary } from '@/types/recommend'

type MarkerType = 'apt' | 'offi'

const MARKER_COLORS: Record<MarkerType, string> = {
  apt:  '#3B82F6', // 파란색
  offi: '#F97316', // 주황색
}

export function useAptMap(containerRef: Ref<HTMLElement | null>) {
  let map: kakao.maps.Map | null = null
  let openInfoWindow: kakao.maps.InfoWindow | null = null

  const markersByType: Record<MarkerType, kakao.maps.Marker[]> = {
    apt:  [],
    offi: [],
  }

  function initMap() {
    kakao.maps.load(() => {
      if (!containerRef.value) return
      map = new kakao.maps.Map(containerRef.value, {
        center: new kakao.maps.LatLng(37.5665, 126.978),
        level: 8,
      })
    })
  }

  function clearMarkers(type?: MarkerType) {
    const types: MarkerType[] = type ? [type] : ['apt', 'offi']
    for (const t of types) {
      markersByType[t].forEach((m) => m.setMap(null))
      markersByType[t].length = 0
    }
    openInfoWindow?.close()
    openInfoWindow = null
  }

  function drawMarkers(complexes: AptComplexSummary[], type: MarkerType = 'apt') {
    if (!map) return
    clearMarkers(type)

    const valid = complexes.filter((c) => c.lat != null && c.lng != null)
    if (valid.length === 0) return

    const avgLat = valid.reduce((s, c) => s + c.lat!, 0) / valid.length
    const avgLng = valid.reduce((s, c) => s + c.lng!, 0) / valid.length
    map.setCenter(new kakao.maps.LatLng(avgLat, avgLng))
    map.setLevel(6)

    const color = MARKER_COLORS[type]

    for (const complex of valid) {
      const position = new kakao.maps.LatLng(complex.lat!, complex.lng!)
      const image = new kakao.maps.MarkerImage(
        svgMarkerUrl(color),
        new kakao.maps.Size(24, 24),
        { offset: new kakao.maps.Point(12, 24) },
      )
      const marker = new kakao.maps.Marker({ position, map: map!, image })
      markersByType[type].push(marker)

      const infoWindow = new kakao.maps.InfoWindow({
        content: buildInfoContent(complex, type),
        removable: true,
      })

      kakao.maps.event.addListener(marker, 'click', () => {
        openInfoWindow?.close()
        infoWindow.open(map!, marker)
        openInfoWindow = infoWindow
      })
    }
  }

  function setMarkersVisible(type: MarkerType, visible: boolean) {
    markersByType[type].forEach((m) => m.setMap(visible ? map : null))
  }

  return { initMap, drawMarkers, clearMarkers, setMarkersVisible }
}

// ─── 헬퍼 함수 ────────────────────────────────────────────────────────────────

function svgMarkerUrl(color: string): string {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
    <path fill="${color}" stroke="white" stroke-width="1.5"
      d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/>
    <circle fill="white" cx="12" cy="9" r="3"/>
  </svg>`
  return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`
}

function buildInfoContent(c: AptComplexSummary, type: MarkerType): string {
  const typeLabel = type === 'apt' ? '아파트' : '오피스텔'
  const color = MARKER_COLORS[type]
  return `
    <div style="padding:12px 16px;min-width:210px;font-family:sans-serif;line-height:1.6">
      <div style="display:flex;align-items:center;gap:6px;margin-bottom:3px">
        <span style="font-size:10px;background:${color};color:white;padding:1px 6px;border-radius:10px">${typeLabel}</span>
        <span style="font-size:14px;font-weight:600;color:#111">${c.name}</span>
      </div>
      <div style="font-size:11px;color:#999;margin-bottom:8px">${c.roadAddr ?? c.dong ?? ''}</div>
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

function formatPrice(manwon: number | null): string {
  if (manwon == null) return '정보 없음'
  const eok = Math.floor(manwon / 10000)
  const rem = manwon % 10000
  if (eok > 0 && rem > 0) return `${eok}억 ${rem.toLocaleString()}만원`
  if (eok > 0) return `${eok}억원`
  return `${rem.toLocaleString()}만원`
}
