package com.example.telegrambot;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;


@Component
@EnableScheduling
public class TelegramBot extends TelegramLongPollingBot {
    private String nameOfBot = "NotificationForCrockBot";
    private static HashMap<Long,String> usersId = new HashMap<>();
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
                case "/checkMembers":
                    try {
                        checkMembers(update.getMessage().getChatId());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
            }
        }
    }
    private synchronized void helloMessage(Long chatId, String name) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        if(!usersId.containsKey(chatId)) {
            sendMessage.setText("Привет, " + name + "! Я тебя добавил в список :) Теперь буду тебя оповещать!");
            usersId.put(chatId,name);
        } else {
            sendMessage.setText("Я тебя уже добавил)");
        }
        sendMessage.setChatId(chatId);
        execute(sendMessage);
    }
    private void sendMessage(Long chatId, String message) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        execute(sendMessage);
    }
    @Scheduled(cron = "0 25 19 * * WED")
    @Scheduled(cron = "0 25 19 * * FRI")
    public void notification() throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Напоминаю про дейлик через 15 минут!!!");
        for(Long usId : usersId.keySet()){
            sendMessage.setChatId(usId);
            execute(sendMessage);
        }
    }
    public void checkMembers(Long chatId) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        for(Long usId: usersId.keySet()){
            sendMessage.setText(usersId.get(usId));
            execute(sendMessage);
        }
    }

}
