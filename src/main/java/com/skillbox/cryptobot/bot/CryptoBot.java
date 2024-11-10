package com.skillbox.cryptobot.bot;

import com.skillbox.cryptobot.model.Subscribers;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class CryptoBot extends TelegramLongPollingCommandBot {

    private final String botUsername;

    private final CryptoCurrencyService service;

    private final Map<Long, Long> timeSendMessage = new HashMap<>();


    public CryptoBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            List<IBotCommand> commandList, CryptoCurrencyService service
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.service = service;

        commandList.forEach(this::register);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
    }


    @Scheduled(cron = "0 0/2 * * * *")
    private void getSubscribersSatisfyingCondition() {
        List<Subscribers> subscribers = service.getAllSubscribe();
        try {
            double priceBitcoin = service.getBitcoinPrice();
            for (Subscribers subscribe : subscribers) {

                boolean sendMessageTime = true;
                try {
                    long now = System.currentTimeMillis();
                    sendMessageTime = (now - timeSendMessage.get(subscribe.getIdClient())) > 3_600_000L;
                } catch (Exception e) {
                    e.getStackTrace();
                }
                if ((subscribe.getPrice() > priceBitcoin) & sendMessageTime) {
                    sendMessage(subscribe.getIdClient());
                    timeSendMessage.put(subscribe.getIdClient(), System.currentTimeMillis());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(Long id) {
        var chatId = String.valueOf(id);
        String text;
        try {
            double price = (double) Math.round(service.getBitcoinPrice()*100);
            price = price/100;
            text = "Цена биткоина снизилась до " + price +" USD";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var message = new SendMessage(chatId, text);
        try {
            execute(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
