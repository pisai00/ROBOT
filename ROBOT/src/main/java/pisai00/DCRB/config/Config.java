package pisai00.DCRB.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getDiscordToken() {
        return dotenv.get("DISCORD_BOT_TOKEN");
    }

    public static String getOpenWeatherMapApiKey() {
        return dotenv.get("OPENWEATHERMAP_API_KEY");
    }

    public static String getGeminiApiKey() {
        return dotenv.get("GEMINI_API_KEY");
    }

    public static String getTranslationApiKey() {
        return dotenv.get("TRANSLATION_API_KEY");
    }
}
