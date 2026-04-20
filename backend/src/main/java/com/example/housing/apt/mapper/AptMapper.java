package com.example.housing.apt.mapper;

import com.example.housing.apt.dto.NearbyComplexDto;
import com.example.housing.apt.model.AptComplex;
import com.example.housing.apt.model.AptOfficialPrice;
import com.example.housing.apt.model.AptRent;
import com.example.housing.apt.model.AptTrade;
import com.example.housing.recommend.dto.AptComplexSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AptMapper {

    // 단지
    AptComplex findComplexById(Long id);
    AptComplex findComplexByNameAndBjdong(@Param("name") String name, @Param("bjdongCode") String bjdongCode);
    AptComplex findComplexByComplexNo(String complexNo);
    List<AptComplex> findComplexesByBjdong(String bjdongCode);
    void insertComplex(AptComplex complex);
    void updateComplexCoords(@Param("id") Long id, @Param("lat") Double lat, @Param("lng") Double lng);
    void updateComplexSigunguAndSido(@Param("id") Long id, @Param("sigungu") String sigungu, @Param("sido") String sido);
    void updateComplexDong(@Param("id") Long id, @Param("dong") String dong);
    List<AptComplex> findComplexesWithoutCoords();

    // 매매 실거래가
    List<AptTrade> findTradesByComplexId(@Param("complexId") Long complexId);
    List<AptTrade> findTradesByBjdong(@Param("bjdongCode") String bjdongCode,
                                      @Param("year") int year,
                                      @Param("month") int month);
    boolean existsTrade(@Param("complexId") Long complexId,
                        @Param("dealYear") int dealYear,
                        @Param("dealMonth") int dealMonth,
                        @Param("dealDay") int dealDay,
                        @Param("area") double area,
                        @Param("floor") int floor);
    void insertTrade(AptTrade trade);

    // 전월세 실거래가
    List<AptRent> findRentsByComplexId(@Param("complexId") Long complexId);
    boolean existsRent(@Param("complexId") Long complexId,
                       @Param("dealYear") int dealYear,
                       @Param("dealMonth") int dealMonth,
                       @Param("area") double area,
                       @Param("floor") int floor,
                       @Param("deposit") int deposit);
    void insertRent(AptRent rent);

    // 공시가격
    List<AptOfficialPrice> findOfficialPricesByComplexAndYear(@Param("complexId") Long complexId,
                                                              @Param("year") int year);
    void insertOfficialPrice(AptOfficialPrice price);

    // 추천용: 시군구 내 단지 + 최근 1년 평균 매매가/전세금
    List<AptComplexSummary> findComplexesWithAvgPriceBySigungu(@Param("sigungu") String sigungu);

    // 위경도 반경 조회: 지정 좌표로부터 radiusKm km 이내 단지 + 최근 1년 평균 매매가/전세금
    List<NearbyComplexDto> findNearbyComplexesWithAvgPrice(
            @Param("lat")      double lat,
            @Param("lng")      double lng,
            @Param("radiusKm") double radiusKm
    );

    // 지도용: 시군구 내 법정동 목록 (드롭다운용)
    List<String> findDongsBySigungu(@Param("sigungu") String sigungu);

    // 지도용: 시군구(+선택적 동) 기준 단지 + 최근 1년 평균 매매가/전세금
    List<AptComplexSummary> findComplexesForMap(
            @Param("sigungu") String sigungu,
            @Param("dong")    String dong
    );
}
