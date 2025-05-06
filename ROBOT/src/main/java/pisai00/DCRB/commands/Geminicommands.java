package pisai00.DCRB.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pisai00.DCRB.tools.TranslateClient;

public class Geminicommands extends ListenerAdapter{
    @Override
        public void onMessageReceived(@SuppressWarnings("null") MessageReceivedEvent event) {
            if (!event.getAuthor().isBot()) { 
                String content = event.getMessage().getContentRaw();
                if (content.startsWith("!translate ")) {
                    String[] args = content.split("\\s+");
                    
                    event.getChannel().sendMessage(TranslateClient.translateText(args[1], args[2])).queue();
                    
                }
            }
        }
}
