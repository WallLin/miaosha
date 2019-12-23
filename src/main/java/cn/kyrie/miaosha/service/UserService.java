package cn.kyrie.miaosha.service;

import cn.kyrie.miaosha.dao.UserDao;
import cn.kyrie.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kyrie
 * @date 2019-12-22 - 20:54
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Transactional
    public boolean insert() {
        User u1 = new User();
        u1.setId(2);
        u1.setName("xiaoxin");
        userDao.insert(u1);

        User u2 = new User();
        u2.setId(1);
        u2.setName("kyrie1");
        userDao.insert(u2);

        return true;
    }
}
