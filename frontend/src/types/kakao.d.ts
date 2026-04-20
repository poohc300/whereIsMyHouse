// 카카오 지도 SDK 전역 네임스페이스 최소 타입 선언
// 필요한 API가 추가될 때 여기에 확장한다.
declare namespace kakao {
  namespace maps {
    /** SDK 비동기 로드 완료 후 callback 실행 (autoload=false 사용 시 필수) */
    function load(callback: () => void): void

    class Map {
      constructor(container: HTMLElement, options: MapOptions)
      setCenter(latlng: LatLng): void
      setLevel(level: number): void
    }

    class LatLng {
      constructor(lat: number, lng: number)
    }

    class Marker {
      constructor(options?: MarkerOptions)
      setMap(map: Map | null): void
    }

    class InfoWindow {
      constructor(options: InfoWindowOptions)
      open(map: Map, marker: Marker): void
      close(): void
    }

    namespace event {
      function addListener(target: object, type: string, handler: () => void): void
    }

    interface MapOptions {
      center: LatLng
      level?: number
    }

    interface MarkerOptions {
      position: LatLng
      map?: Map | null
    }

    interface InfoWindowOptions {
      content: string
      removable?: boolean
    }
  }
}
