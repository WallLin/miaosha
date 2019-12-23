package cn.kyrie.miaosha.dao;

import cn.kyrie.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author kyrie
 * @date 2019-12-22 - 20:46
 */
@Mapper
public interface UserDao {
    @Select("select * from user where id = #{id}")
    public User getUserById(@Param("id") int id);

    @Insert("insert into user(id, name) values(#{id}, #{name})")
    public void insert(User user);
}
