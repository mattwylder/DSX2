package book;

import java.util.HashMap;

import price.InvalidPriceOperation;
import tradable.Tradable;
import message.FillMessage;
import message.InvalidDataOperation;

public interface TradeProcessor {
	public HashMap<String, FillMessage> doTrade(Tradable trd) throws InvalidDataOperation, InvalidPriceOperation;
}
