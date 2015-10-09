package persistence;

import com.lv.springboot.Application;
import com.lv.springboot.persistence.Transactor;
import com.lv.springboot.persistence.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static util.TestData.normalUserPaul;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private Transactor transactor;

    @Test
    public void canInsertUsers() {
        transactor.runAndRollback(status -> {
            userDao.insert(normalUserPaul());

            final Map<String, Object> user = userDao.userFor(BigDecimal.ONE).get();
            assertThat(user).contains(entry("firstname", "Paul"), entry("lastname", "Denver"));
        });
    }
}
