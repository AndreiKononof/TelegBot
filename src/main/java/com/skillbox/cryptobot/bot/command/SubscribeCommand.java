package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.exception.NotFoundException;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SubscribeCommand implements IBotCommand {

    private final CryptoCurrencyService service;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        String messageToClient;

        String regex = "[^\\d.,]";
        double price = 0;
        if (!message.getText().replaceAll(regex, "").isEmpty()) {
            price = Double.parseDouble(message.getText().replaceAll(regex, ""));
        }
        try {
            if (price != 0) {
                service.updateClientPrise(message.getChatId(), price);
                messageToClient = "Подписались на цену " + price + " USD";
            } else {
                messageToClient = "Не указанна желаемая цена биткоина\n" +
                        "Необходимо вести команду повторно с желаемым числом в формате 0.00 (Измерения в USD)";
            }
        } catch (NotFoundException ex) {
            messageToClient = "На текущий момент вы не зарегистрировались  :( \n " +
                    "Для продолжение используйте команду /start";
        }

        try {
            answer.setText(messageToClient);
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка возникла /get_subscription методе", e);
        }
    }
}