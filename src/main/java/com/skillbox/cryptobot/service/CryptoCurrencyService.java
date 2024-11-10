package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.client.BinanceClient;
import com.skillbox.cryptobot.exception.NotFoundException;
import com.skillbox.cryptobot.model.Subscribers;
import com.skillbox.cryptobot.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class CryptoCurrencyService {

    private final AtomicReference<Double> price = new AtomicReference<>();
    private final BinanceClient client;
    private final SubscriptionRepository repository;


    public CryptoCurrencyService(BinanceClient client, SubscriptionRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    public double getBitcoinPrice() throws IOException {
        if (price.get() == null) {
            price.set(client.getBitcoinPrice());
        }
        return price.get();
    }

    public void saveClientSubscribe(Long idClient){
        Subscribers subscribers = new Subscribers();
        subscribers.setIdClient(idClient);
        repository.save(subscribers);
    }

    public void updateClientPrise(Long idClient, Double price){
        List<Subscribers> subscribers = repository.findByWhereIdClient(idClient);
        if(subscribers.isEmpty()){
            throw new NotFoundException(MessageFormat.format("Client with ID {0} not found ", idClient));
        }
        Subscribers subscribe = subscribers.get(0);
        subscribe.setPrice(price);
        repository.save(subscribe);
    }

    public Subscribers findByClient(Long idClient){
         List<Subscribers> subscribers = repository.findByWhereIdClient(idClient);
        if(subscribers.isEmpty() ){
            return null;
        }
        return subscribers.get(0);
    }

    public List<Subscribers> getAllSubscribe(){
        return repository.findAll();
    }
}
