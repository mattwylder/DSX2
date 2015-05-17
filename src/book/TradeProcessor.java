package book;

import java.util.HashMap;

import tradable.Tradable;
import message.FillMessage;

public interface TradeProcessor {
	public HashMap<String, FillMessage> doTrade(Tradable trd);
}
