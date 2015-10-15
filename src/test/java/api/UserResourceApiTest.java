package api;

import com.github.toastshaman.springboot.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static util.TestData.normalUserKevin;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest({"server.port = 9091", "management.port = 9092"})
@ActiveProfiles("local")
public class UserResourceApiTest {

    private final RestTemplate restTemplate = new TestRestTemplate();

    @Test
    @SuppressWarnings("unchecked")
    public void savesUsers() {
        final Map response = restTemplate.postForObject("http://localhost:9091/users", normalUserKevin(), Map.class);
        assertThat(response).contains(entry("status", "ok"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void returnsUsers() {
        restTemplate.postForObject("http://localhost:9091/users", normalUserKevin(), Map.class);
        final Map response = restTemplate.getForObject("http://localhost:9091/users/1", Map.class);
        assertThat(response).contains(entry("firstname", "Kevin"), entry("lastname", "Denver"));
    }
}
