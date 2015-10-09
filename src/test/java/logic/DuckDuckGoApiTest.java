package logic;

import com.github.dreamhead.moco.HttpServer;
import com.lv.springboot.Application;
import com.lv.springboot.externals.DuckDuckGoApi;
import com.lv.springboot.util.UnirestWrapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static alexh.Unchecker.uncheckedGet;
import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.running;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static util.FileUtils.fileToString;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class DuckDuckGoApiTest {

    @Autowired
    private DuckDuckGoApi duckDuckGo;

    @BeforeClass
    public static void setup() {
        UnirestWrapper.configure();
    }

    @Test
    public void successfullyCallsTheApi() throws Exception {
        final String jsonResponse = uncheckedGet(() -> fileToString(DuckDuckGoApiTest.class, "duckduckgo-response-london.json"));
        final HttpServer server = httpServer(12306);
        server.get(eq(query("q"), "London")).response(with(text(jsonResponse)), header("Content-Type", "application/json"));

        running(server, () -> {
            final Map london = duckDuckGo.zeroClickInfo("London");
            assertThat(london).contains(entry("Heading", "London"));
        });
    }
}
