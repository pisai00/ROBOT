package pisai00.DCRB.commands;
import pisai00.DCRB.config.Config;
import pisai00.DCRB.tools.GeminiClient;
import pisai00.DCRB.tools.TranslateClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Weather extends ListenerAdapter {
    private final String apiKey = Config.getOpenWeatherMapApiKey(); 
    private final String apiUrl = "https://api.openweathermap.org/data/2.5/weather";
    @Override
        public void onMessageReceived(@SuppressWarnings("null") MessageReceivedEvent event) {
            if (!event.getAuthor().isBot()) { 
                String content = event.getMessage().getContentRaw();
                if (content.startsWith("!weather ")) {
                    String city = content.substring("!weather ".length()).trim();
                    if (city.isEmpty()) {
                        event.getChannel().sendMessage("城市不可為空").queue();
                        return;
                    }
                    if(city.matches("^[a-zA-Z\\s'-]+$")){
                        fetchWeather(event, city);
                    }else {
                        city=TranslateClient.translateText(city,"en");
                        event.getChannel().sendMessage("本功能僅限於英文，已自動翻譯為："+city).queue();
                        fetchWeather(event, city);
                    }
                }
            }

        }
        private void fetchWeather(MessageReceivedEvent event, String city) {
        HttpClient client = HttpClient.newHttpClient();
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String requestUrl = String.format("%s?q=%s&appid=%s&units=metric&lang=zh_tw", apiUrl, encodedCity, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        String responseBody = response.body();
                        parseWeather(event, responseBody);
                    } else if(response.statusCode() == 404){
                        event.getChannel().sendMessage("找不到輸入的城市").queue();
                    }else{
                        event.getChannel().sendMessage("無法取得天氣資訊。錯誤代碼：" + response.statusCode()).queue();
                        System.err.println("天氣 API 請求失敗，狀態碼：" + response.statusCode() + ", 回應：" + response.body());
                    }
                })
                .exceptionally(e -> {
                    event.getChannel().sendMessage("連接天氣 API 時發生錯誤：" + e.getMessage()).queue();
                    e.printStackTrace();
                    return null;
                });
    }

    private void parseWeather(MessageReceivedEvent event, String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            String cityName = jsonObject.get("name").getAsString();
            JsonObject main = jsonObject.getAsJsonObject("main");
            double temperature = main.get("temp").getAsDouble();
            double feelsLike = main.get("feels_like").getAsDouble();
            int humidity = main.get("humidity").getAsInt();
            JsonObject weather = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject();
            String description = weather.get("description").getAsString();
            double wind = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
            double lon = jsonObject.getAsJsonObject("coord").get("lon").getAsDouble();
            double lat = jsonObject.getAsJsonObject("coord").get("lat").getAsDouble();
            String suggestion = GeminiClient.suggestText(cityName, temperature, feelsLike, humidity,wind ,description);
            String weatherInfo = String.format("📍 **%s**\n🌡️ 溫度：%.1f°C (體感溫度：%.1f°C)\n💧 濕度：%d%%\n💨 風速：%s\n🌤️ 天氣狀況：%s\n🔔 提醒：%s(來源於gemini)\n🔗 https://openweathermap.org/weathermap?basemap=map&cities=true&layer=temperature&lat=%s&lon=%s&zoom=8",
                    cityName, temperature, feelsLike, humidity,wind, description ,suggestion ,lat , lon);
            event.getChannel().sendMessage(weatherInfo).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("解析天氣資訊時發生錯誤。").queue();
            e.printStackTrace();
        }
    }


}