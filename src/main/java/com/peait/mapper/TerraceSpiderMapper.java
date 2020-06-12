package com.peait.mapper;

import com.peait.entity.TerraceSpider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TerraceSpiderMapper {
    int deleteByPrimaryKey(String id);

    int insert(TerraceSpider record);

    int insertSelective(TerraceSpider record);

    TerraceSpider selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(TerraceSpider record);

    int updateByPrimaryKey(TerraceSpider record);

    List<TerraceSpider> selectByTerraceName(@Param("terraceName") String mashi);
}