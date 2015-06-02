package client;
import java.util.ArrayList;

import book.InvalidMarketStateException;
import book.NoSuchProductException;
import price.InvalidPriceOperation;
import price.Price;
import publisher.AlreadySubscribedException;
import tradable.TradableDTO;
import message.*;

public interface User {
	String getUserName();
	void acceptLastSale(String product, Price p, int v);
	void acceptMessage(FillMessage fm);
	void acceptMessage(CancelMessage cm);
	void acceptTicker(String product, Price p, char direction);
	void acceptCurrentMarket(String product, Price bp, int bv, Price sp, int sv);
	void acceptMarketMessage(String message);
	void connect() throws AlreadyConnectedException, UserNotConnectedException, InvalidConnectionIdException;
	void disconnect() throws UserNotConnectedException, InvalidConnectionIdException;
	void showMarketDisplay() throws UserNotConnectedException, Exception;
	String submitOrder(String product, Price price, int volume, String side) throws InvalidDataOperation, UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateException, NoSuchProductException, InvalidPriceOperation;
	void submitOrderCancel(String product, String side, String orderId) throws UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateException, NoSuchProductException;
	void submitQuote(String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws UserNotConnectedException, InvalidConnectionIdException, InvalidDataOperation, InvalidPriceOperation, InvalidMarketStateException, NoSuchProductException;
	void submitQuoteCancel(String product) throws UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateException, NoSuchProductException, InvalidDataOperation, InvalidPriceOperation;
	void subscribeCurrentMarket(String product) throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException;
	void subscribeLastSale(String product) throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException;
	void subscribeMessages(String product) throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException;
	void subscribeTicker(String product) throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException;
	Price getAllStockValue() throws InvalidPriceOperation;
	Price getAccountCosts();
	Price getNetAccountValue() throws InvalidPriceOperation;
	String[][] getBookDepth(String product) throws UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException;
	String getMarketState() throws UserNotConnectedException, InvalidConnectionIdException;
	ArrayList<TradableUserData> getOrderIds();
	ArrayList<String> getProductList();
	Price getStockPositionValue(String sym) throws InvalidPriceOperation;
	int getStockPositionVolume(String product);
	ArrayList<String> getHoldings();
	ArrayList<TradableDTO> getOrdersWithRemainingQty(String product) throws UserNotConnectedException, InvalidConnectionIdException, InvalidDataOperation, InvalidPriceOperation;
	
}
