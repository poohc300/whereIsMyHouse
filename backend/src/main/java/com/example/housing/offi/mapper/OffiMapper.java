package com.example.housing.offi.mapper;

import com.example.housing.offi.model.OffiComplex;
import com.example.housing.offi.model.OffiRent;
import com.example.housing.offi.model.OffiTrade;
import com.example.housing.recommend.dto.AptComplexSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OffiMapper {

    OffiComplex findComplexByNameAndBjdong(@Param("name") String name, @Param("bjdongCode") String bjdongCode);
    void insertComplex(OffiComplex complex);
    void updateComplexCoords(@Param("id") Long id, @Param("lat") Double lat, @Param("lng") Double lng);

    boolean existsTrade(@Param("complexId") Long complexId,
                        @Param("dealYear") int dealYear,
                        @Param("dealMonth") int dealMonth,
                        @Param("dealDay") int dealDay,
                        @Param("area") double area,
                        @Param("floor") int floor);
    void insertTrade(OffiTrade trade);

    boolean existsRent(@Param("complexId") Long complexId,
                       @Param("dealYear") int dealYear,
                       @Param("dealMonth") int dealMonth,
                       @Param("area") double area,
                       @Param("floor") int floor,
                       @Param("deposit") int deposit);
    void insertRent(OffiRent rent);

    List<AptComplexSummary> findComplexesForMap(@Param("sigungu") String sigungu,
                                                @Param("dong") String dong);
}
