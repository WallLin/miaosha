package cn.kyrie.miaosha.dao;

import cn.kyrie.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author kyrie
 * @date 2019-12-24 - 22:34
 */
@Mapper
public interface MiaoshaUserDao {

    /**
     * 根据手机号查询用户信息
     * @param id
     * @return
     */
    @Select("select * from miaosha_user where id = #{id}")
    public MiaoshaUser getById(@Param("id") long id);
}
