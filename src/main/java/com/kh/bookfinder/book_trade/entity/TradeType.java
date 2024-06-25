package com.kh.bookfinder.book_trade.entity;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.InvalidFieldException;

public enum TradeType {
  LEND,
  BORROW;

  public static TradeType fromStringIgnoreCase(String value) {
    for (TradeType type : TradeType.values()) {
      if (type.name().equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new InvalidFieldException("tradeType", Message.INVALID_TRADE_TYPE);
  }
}
