package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.model.Subscribers;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {

    private final CryptoCurrencyService service;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        String messageToClient;
        Subscribers subscribers = service.findByClient(message.getChatId());
        if (subscribers != null) {
            if (subscribers.getPrice() != 0) {
                messageToClient = "Подписаны на цену " + subscribers.getPrice() + " USD";
            } else {
                messageToClient = "На текущий момент подписки еще нет :( \n " +
                        "Для подписки используйте команду /subscribe [число]";
            }
        } else {
            messageToClient = "На текущий момент вы не зарегистрировались  :( \n " +
                    "Для продолжение используйте команду /start";
        }
        try {
            answer.setText(messageToClient);
            absSender.execute(answer);
        } catch (Exception e) {
            log.error("Ошибка возникла /get_subscription методе", e);
        }
    }
}
