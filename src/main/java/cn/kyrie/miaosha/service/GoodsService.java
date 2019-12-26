package cn.kyrie.miaosha.service;

import cn.kyrie.miaosha.dao.GoodsDao;
import cn.kyrie.miaosha.domain.MiaoshaGoods;
import cn.kyrie.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kyrie
 * @date 2019-12-22 - 20:54
 */
@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 减库存
     * @param goodsId
     */
    public void reduceStock(long goodsId) {
        goodsDao.reduceStockByGoodsId(goodsId);
    }
}
