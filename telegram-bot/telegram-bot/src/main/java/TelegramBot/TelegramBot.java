package TelegramBot;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Component
@EnableScheduling
public class TelegramBot extends TelegramLongPollingBot {
    private String nameOfBot = "NotificationForCrockBot";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private LocalDateTime dateOfDeilic;
    private static int flagDate = 0;
    Long chatId;
    private String token = "5411274648:AAG8S_fiUkZZ4EUbmubGOIkM2BnTN6Wq0UY";


    @Override
    public String getBotUsername() {
        return nameOfBot;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String message = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            String name = update.getMessage().getChat().getFirstName();
            switch (message) {
                case "/start":
                    try {
                        helloMessage(chatId, name);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "/setDate":
                    try {
                        sendMessage(chatId,"Введите дату дейлика в таком формате дд-мм-гг чч:мм");
                        flagDate = 1;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "/getDate":
                    try {
                        dateMessage(chatId);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                default:
                    if(flagDate == 1){
                        dateOfDeilic = LocalDateTime.parse(update.getMessage().getText(),formatter);

                        try {
                            sendMessage(chatId,dateOfDeilic.toString());
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
            }
        }
    }
    private void helloMessage(Long chatId, String name) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Hi " + name + "!");
        sendMessage.setChatId(chatId);
        execute(sendMessage);
    }
    private void sendMessage(Long chatId, String message) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        execute(sendMessage);
    }
    private void dateMessage(Long chatId) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if(dateOfDeilic != null){
            sendMessage.setText("День: " + dateOfDeilic.getDayOfMonth()+"/"+dateOfDeilic.getMonth()+"/"+dateOfDeilic.getYear() + " Время: " + dateOfDeilic.getHour() + ":" + dateOfDeilic.getMinute());
        } else {
            sendMessage.setText("Дата пока ещё не задана!");
        }
        execute(sendMessage);
    }
//    @Scheduled(cron = "0 17 17 ")
//    public void notification(){
//
//    }

}
