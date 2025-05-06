package pisai00.DCRB.tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.dv8tion.jda.api.entities.Activity;

public class GetActivity {
    private static final String filePath = "botstatuses.txt";
    public static Activity getramdonActivity() {
        List<String> lines = new ArrayList<>();
        ClassLoader classLoader = GetActivity.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                System.err.println("找不到檔案：" + filePath);
                return null;
            }

            try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(streamReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("讀取檔案時發生錯誤：" + e.getMessage());
            return null;
        }

        if (lines.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(lines.size());
        return Activity.customStatus(lines.get(randomIndex));
    }
}