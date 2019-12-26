package cn.kyrie.miaosha.dao;

import cn.kyrie.miaosha.domain.MiaoshaGoods;
import cn.kyrie.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author kyrie
 * @date 2019-12-24 - 22:34
 */
@Mapper
public interface GoodsDao {

    @Select("select g.*, mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date from miaosha_goods mg left join goods g on mg.id = g.id")
    List<GoodsVo> listGoodsVo();

    @Select("select g.*, mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date from miaosha_goods mg left join goods g on mg.id = g.id where g.id = #{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") Long goodsId);

    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId}")
    int reduceStockByGoodsId(@Param("goodsId") long goodsId);
}
