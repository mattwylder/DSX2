package client;
import price.Price;
import message.*;

public interface User {
	String getUserName();
	void acceptLastSale(String product, Price p, int v);
	void acceptMessage(FillMessage fm);
	void acceptMessage(CancelMessage cm);
	void acceptTicker(String product, Price p, char direction);
	void acceptCurrentMarket(String product, Price bp, int bv, Price sp, int sv);
	void acceptMarketMessage(String message);
}
