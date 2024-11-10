package com.skillbox.cryptobot.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "subscribers")
@Getter
@Setter
public class Subscribers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "id_client")
    private long idClient;

    @JoinColumn(name = "price")
    private double price;

}
