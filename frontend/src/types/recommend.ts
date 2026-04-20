export type AgeGroup = 'TWENTIES' | 'THIRTIES' | 'FORTIES' | 'OVER_FIFTIES'
export type MaritalStatus = 'SINGLE' | 'MARRIED_WITHIN_7Y' | 'MARRIED_OVER_7Y'
export type HouseOwnership = 'NONE' | 'ONE' | 'TWO_OR_MORE'
export type AnnualIncomeRange = 'UNDER_30M' | 'RANGE_30M_50M' | 'RANGE_50M_70M' | 'RANGE_70M_100M' | 'OVER_100M'
export type TotalAssetsRange = 'UNDER_100M' | 'RANGE_100M_300M' | 'RANGE_300M_500M' | 'OVER_500M'
export type SubscriptionPeriod = 'NONE' | 'UNDER_1Y' | 'RANGE_1Y_2Y' | 'RANGE_2Y_5Y' | 'RANGE_5Y_10Y' | 'OVER_10Y'

export interface RecommendRequest {
  ageGroup: AgeGroup
  maritalStatus: MaritalStatus
  childCount: number
  houseOwnership: HouseOwnership
  annualIncomeRange: AnnualIncomeRange
  totalAssetsRange: TotalAssetsRange
  subscriptionPeriod: SubscriptionPeriod
  preferredSigungu: string
}

export interface EligibilityResult {
  nationalHousing: boolean
  privateHousing: boolean
  specialNewlywed: boolean
  specialFirstTime: boolean
  specialMultiChild: boolean
}

export interface PriorityScore {
  noHousePeriodScore: number
  dependentsScore: number
  subscriptionPeriodScore: number
  total: number
  maxScore: number
}

export interface SubscriptionAnnouncement {
  id: number
  announceNo: string
  houseName: string
  houseType: string
  supplyAddr: string
  totalSupply: number | null
  receptionStartDt: string
  receptionEndDt: string
  winnerAnnounceDt: string | null
  minPrice: number | null
  maxPrice: number | null
}

export interface AptComplexSummary {
  id: number
  name: string
  roadAddr: string
  sigungu: string
  dong: string
  lat: number | null
  lng: number | null
  avgTradePrice: number | null
  avgDepositPrice: number | null
}

export interface RecommendResponse {
  eligibility: EligibilityResult
  priorityScore: PriorityScore
  activeSubscriptions: SubscriptionAnnouncement[]
  aptRecommendations: AptComplexSummary[]
}
