package com.peait.entity;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class TerraceSpider {
    private String id;

    private Byte isSend;

    private String terraceName;

    private String projectTitle;

    private String projectDescription;

    private BigDecimal projectPrice;


}