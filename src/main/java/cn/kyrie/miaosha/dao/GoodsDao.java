package cn.kyrie.miaosha.dao;

import cn.kyrie.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author kyrie
 * @date 2019-12-24 - 22:34
 */
@Mapper
public interface GoodsDao {

    @Select("select g.*, mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date from miaosha_goods mg left outer join goods g on mg.id = g.id")
    List<GoodsVo> listGoodsVo();
}
