package com.example.housing.collect.model;

/**
 * 공공 API 호출에 사용하는 지역 코드 단위.
 * lawdCd = 법정동코드 앞 5자리 (시군구 단위)
 */
public record RegionCode(String sido, String sigungu, String lawdCd) {}
