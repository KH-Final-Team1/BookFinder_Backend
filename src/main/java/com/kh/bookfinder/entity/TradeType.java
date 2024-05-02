package com.kh.bookfinder.entity;

public enum TradeType {
  RENT,
  BORROW;

  public static TradeType fromString(String value) {
    if (value != null) {
      for (TradeType tradeType : TradeType.values()) {
        if (value.equalsIgnoreCase(tradeType.name())) {
          return tradeType;
        }
      }
    }
    throw new IllegalArgumentException("Invalid TradeType: " + value);
  }
}
