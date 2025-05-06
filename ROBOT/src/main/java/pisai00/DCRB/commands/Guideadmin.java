package pisai00.DCRB.commands;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;


public class Guideadmin extends ListenerAdapter {

    private static final String CONFIRM_EMOJI = "✅";
    private static final String CANCEL_EMOJI = "❌";
    private static final long CONFIRMATION_TIMEOUT = 30; // 秒

    @Override
    public void onMessageReceived(@SuppressWarnings("null") MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) { 
            String content = event.getMessage().getContentRaw();
            if (content.startsWith("!purge ")) {
                TextChannel channel = (TextChannel) event.getChannel();
                String[] args = content.split("\\s+");

                if (args.length == 2 && args[1].matches("\\d+")) {
                    int amountToDelete = Math.min(Integer.parseInt(args[1]), 100);
                    channel.getIterableHistory()
                            .takeAsync(amountToDelete)
                            .thenAccept(messages -> {
                                if (messages.isEmpty()) {
                                    event.getChannel().sendMessage("找不到可以刪除的訊息。").queue();
                                    return;
                                }
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                if(messages.get(messages.size()-1).getTimeCreated().isBefore(OffsetDateTime.now().minus(2, ChronoUnit.WEEKS))){
                                    event.getChannel().sendMessage("由於 Discord 的規範限制，無法批量刪除超過 2 週的訊息。").queue();
                                    return;
                                }
                                if(messages.size()<2){
                                    event.getChannel().sendMessage("由於 Discord 的規範限制，無法批量刪除低於2則訊息。").queue();
                                    return;
                                }
                                StringBuilder confirmationMessage = new StringBuilder("你即將刪除以下訊息("+messages.get(messages.size()-1).getTimeCreated().format(formatter)+")以後的"+messages.size()+"條訊息：\n");
                                confirmationMessage.append(messages.get(messages.size()-1).getAuthor().getName())
                                        .append(": ")
                                        .append(messages.get(messages.size()-1).getContentRaw())
                                        .append("\n");
                                event.getChannel().sendMessage(confirmationMessage.toString()).queue(confirm -> {
                                    confirm.addReaction(Emoji.fromUnicode(CONFIRM_EMOJI)).queue();
                                    confirm.addReaction(Emoji.fromUnicode(CANCEL_EMOJI)).queue();
                                    event.getJDA().addEventListener(new ListenerAdapter() {
                                        @SuppressWarnings("null")
                                        @Override
                                        public void onMessageReactionAdd(@SuppressWarnings("null") MessageReactionAddEvent reactionEvent) {
                                            if (reactionEvent.getUser().getIdLong() == event.getAuthor().getIdLong() &&
                                                reactionEvent.getMessageIdLong() == confirm.getIdLong()) {

                                                if (reactionEvent.getEmoji().getName().equals(CONFIRM_EMOJI)) {
                                                    // 刪除原始指令和要被刪除的訊息 (不包括確認訊息)
                                                    channel.deleteMessages(messages).queue(success -> {
                                                        confirm.delete().queue(); // 刪除確認訊息
                                                        event.getMessage().delete().queue(); // 刪除觸發指令
                                                    }, error -> {
                                                        if (error instanceof IllegalArgumentException && error.getMessage().contains("older than 2 weeks")) {
                                                            confirm.editMessage("由於 Discord 的規範限制，無法批量刪除超過 2 週的訊息。").queue();
                                                        } else {
                                                            confirm.editMessage("刪除訊息失敗，請檢查機器人權限。").queue();
                                                            error.printStackTrace();
                                                        }
                                                    });
                                                } else if (reactionEvent.getEmoji().getName().equals(CANCEL_EMOJI)) {
                                                    confirm.editMessage("刪除操作已取消。").queue();
                                                }
                                                event.getJDA().removeEventListener(this); // 移除監聽器
                                            }
                                        }
                                    });
                                    confirm.delete().queueAfter(CONFIRMATION_TIMEOUT, TimeUnit.SECONDS, success -> {
                                        if (event.getJDA().getEventManager().getRegisteredListeners().contains(this)) {
                                            event.getJDA().removeEventListener(this);
                                            event.getChannel().sendMessage("刪除確認超時，操作已取消。").queue();
                                        }
                                    }, error -> {});

                                });
                                
                            });
                                
                }
            }
        }
    }

}
