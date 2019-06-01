package io.opentracing.contrib.specialagent.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Utils {
    public static File savePropertiesToTempFile(Properties props) throws IOException {
        File file = null;
        try {
            file = File.createTempFile("myconfig", "properties");
            try (FileOutputStream stream = new FileOutputStream(file)) {
                props.store(stream, "");
            }

        } catch (Exception e) {
            if (file != null)
                file.delete();

            throw e;
        }

        return file;
    }
}
