package com.lin101.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lin101.store.entity.StoreProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StoreProductMapper extends BaseMapper<StoreProduct> {

    @Select("SELECT sp.*, p.name as product_name, p.image_url, p.category_id " +
            "FROM store_products sp " +
            "LEFT JOIN products p ON sp.product_id = p.product_id " +
            "WHERE sp.store_id = #{storeId}")
    List<Map<String, Object>> getStoreProductsWithInfo(@Param("storeId") Integer storeId);
}
