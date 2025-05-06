package pisai00.DCRB.tools;



import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import pisai00.DCRB.config.Config;

public class GeminiClient {
    private final static String apiKey = Config.getGeminiApiKey();
    private final static String modelName = "gemini-1.5-flash-001";
    private static String generateText(String prompt) {
        Client client = Client.builder().apiKey(apiKey).build();
        GenerateContentResponse response = client.models.generateContent(modelName, prompt, null);
        return response.text();
    }


    public static String suggestText(String cityName,double temperature,double feelsLike,Integer humidity,double wind,String description) {
        String prompt = String.format(" **%s** 溫度：%.1f°C (體感溫度：%.1f°C) 濕度：%d%% 風速：%f%% 天氣狀況：%s 請給一個三句話的建議",cityName, temperature, feelsLike, humidity,wind, description);
        return generateText(prompt);
    }


    

}
