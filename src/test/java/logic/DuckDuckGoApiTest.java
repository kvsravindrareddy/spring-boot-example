package logic;

import com.github.dreamhead.moco.HttpServer;
import com.github.toastshaman.springboot.Application;
import com.github.toastshaman.springboot.externals.DuckDuckGoApi;
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

    @Test
    public void fallbackToAnEmptyMap() throws Exception {
        final HttpServer server = httpServer(12306);
        server.get(eq(query("q"), "London")).response(status(500));

        running(server, () -> {
            final Map london = duckDuckGo.zeroClickInfo("London");
            assertThat(london).isEmpty();
        });
    }
}
