package pisai00.DCRB;
import pisai00.DCRB.config.Config;
import pisai00.DCRB.tools.GetActivity;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import pisai00.DCRB.commands.Geminicommands;
import pisai00.DCRB.commands.Guideadmin;
import pisai00.DCRB.commands.Weather;

import java.io.FileNotFoundException;

import javax.security.auth.login.LoginException;

public class App extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, FileNotFoundException {
        String token = Config.getDiscordToken();
        // 建立 JDA 實例
        @SuppressWarnings("unused")
        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .setActivity(GetActivity.getramdonActivity())
                .addEventListeners(new Guideadmin(),new App(),new Weather(),new Geminicommands())
                .build();
    }

    @Override
    public void onMessageReceived(@SuppressWarnings("null") MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) { 
            String content = event.getMessage().getContentRaw();
            if (content.startsWith("!ping")) {
                event.getChannel().sendMessage("Pong!").queue();
            }else if (content.startsWith("!help")){
                event.getChannel().sendMessage("Pong!").queue();
            }
        }
    }
    
}