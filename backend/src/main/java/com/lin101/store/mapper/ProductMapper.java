package com.lin101.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lin101.store.entity.Product;
import com.lin101.store.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT p.*, sp.store_price as price, sp.stock, sp.status FROM products p " +
            "JOIN store_products sp ON p.product_id = sp.product_id " +
            "WHERE sp.store_id = #{storeId} AND sp.status = 1 " +
            "AND (#{categoryId} IS NULL OR p.category_id = #{categoryId})")
    List<ProductVO> getStoreProducts(@Param("storeId") Integer storeId, @Param("categoryId") Integer categoryId);

    @Select("SELECT p.*, sp.store_price as price, sp.stock, sp.status FROM products p " +
            "JOIN store_products sp ON p.product_id = sp.product_id " +
            "WHERE sp.store_id = #{storeId} AND p.product_id = #{productId}")
    ProductVO getStoreProductById(@Param("storeId") Integer storeId, @Param("productId") Integer productId);

    @Select("SELECT p.*, sp.store_price as price, sp.stock, sp.status FROM products p " +
            "JOIN store_products sp ON p.product_id = sp.product_id " +
            "WHERE sp.store_id = #{storeId} AND sp.status = 1 AND p.is_flash_sale = 1 " +
            "AND p.flash_sale_end_time > NOW() AND sp.stock > 0")
    List<ProductVO> getStoreFlashSaleProducts(@Param("storeId") Integer storeId);

    @Select("SELECT p.*, sp.store_price as price, sp.stock, sp.status FROM products p " +
            "JOIN store_products sp ON p.product_id = sp.product_id " +
            "WHERE sp.store_id = #{storeId} AND sp.status = 1 " +
            "ORDER BY p.product_id DESC LIMIT 10")
    List<ProductVO> getStoreNewArrivals(@Param("storeId") Integer storeId);
}