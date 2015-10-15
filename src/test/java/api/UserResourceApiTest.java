package api;

import com.github.toastshaman.springboot.Application;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static util.TestData.normalUserKevin;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest({"server.port = 9091", "management.port = 9092"})
@ActiveProfiles("local")
public class UserResourceApiTest {

    @Test
    @SuppressWarnings("unchecked")
    public void savesUsers() {
        given().contentType(JSON).body(normalUserKevin()).post("http://localhost:9091/users").then()
            .assertThat()
            .body("status", equalTo("ok"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void returnsUsers() {
        insert(normalUserKevin());

        given().contentType(JSON).get("http://localhost:9091/users/1").then()
            .assertThat()
            .body("firstname", equalTo("Kevin"))
            .body("lastname", equalTo("Denver"));
    }

    private void insert(Map user) {
        final Response response = given().contentType("application/json").body(user)
            .post("http://localhost:9091/users").andReturn();
        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
