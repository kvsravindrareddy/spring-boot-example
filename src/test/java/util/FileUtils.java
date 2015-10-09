package util;

import com.google.common.io.Resources;
import java.io.IOException;
import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;

public class FileUtils {

    public static String fileToString(Class<?> resource, String name) {
        try {
            return Resources.toString(getResource(resource, name), UTF_8);
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    private FileUtils() {}
}
