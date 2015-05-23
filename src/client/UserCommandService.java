package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import message.InvalidDataOperation;
import price.InvalidPriceOperation;
import price.Price;
import publisher.AlreadySubscribedException;
import publisher.CurrentMarketPublisher;
import publisher.LastSalePublisher;
import publisher.MessagePublisher;
import publisher.NotSubscribedException;
import publisher.TickerPublisher;
import tradable.Order;
import tradable.Quote;
import tradable.TradableDTO;
import book.InvalidMarketStateException;
import book.NoSuchProductException;
import book.ProductService;

public class UserCommandService {
	
	private static UserCommandService instance;
	private HashMap<String, Long> connectedUserIds;
	private HashMap<String, User> connectedUsers;
	private HashMap<String, Long> connectedTime;
	
	private UserCommandService(){
		connectedUserIds = new HashMap<String, Long>();
		connectedUsers = new HashMap<String, User>();
		connectedTime = new HashMap<String, Long>();
	}
	
	public static UserCommandService getInstance(){
		if(instance == null){
			instance = new UserCommandService();
		}
		return instance;
	}
	
	private void verifyUser(String userName, long connId) 
			throws UserNotConnectedException,InvalidConnectionIdException{
		if(!connectedUserIds.containsKey(userName)){
			throw new UserNotConnectedException("User " + userName + " not connected");
		}
		if(connId != connectedUserIds.get(userName)){
			throw new InvalidConnectionIdException(userName + " Id " + 
					connId + " does not match stored value: " + connectedUserIds.get(userName));
		}	
	}
	public synchronized long connect(User user) 
			throws AlreadyConnectedException{
		if(connectedUserIds.containsKey(user.getUserName())){
			throw new AlreadyConnectedException("User " + user.getUserName() + " is already connected");
		}
		long nanotime = System.nanoTime();
		connectedUserIds.put(user.getUserName(), nanotime);
		connectedUsers.put(user.getUserName(), user);
		connectedTime.put(user.getUserName(), System.currentTimeMillis());
		return nanotime;
	}
	
	public synchronized void disconnect(String userName, long connId) 
			throws UserNotConnectedException, InvalidConnectionIdException{
		verifyUser(userName, connId);
		connectedUserIds.remove(userName);
		connectedUsers.remove(userName);
		connectedTime.remove(userName);
	}
	
	public String[][] getBookDepth(String userName, long connId, String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException{
		verifyUser(userName, connId);
		return ProductService.getInstance().getBookDepth(product);
	}
	
	public String getMarketState(String userName, long connId) 
			throws UserNotConnectedException, InvalidConnectionIdException{
		verifyUser(userName, connId);
		return ProductService.getInstance().getMarketState().toString();
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName, long connId, String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, InvalidDataOperation, InvalidPriceOperation{
		verifyUser(userName, connId);
		return ProductService.getInstance().getOrdersWithRemainingQty(userName, product);
	}
	
	public ArrayList<String> getProducts(String userName, long connId)
			throws UserNotConnectedException, InvalidConnectionIdException{
		verifyUser(userName, connId);
		ArrayList<String> productList = ProductService.getInstance().getProductList();
		Collections.sort(productList);
		return productList;
	}
	
	public String submitOrder(String userName, long connId, String product, Price price, int volume,
			String side)
					throws InvalidDataOperation, UserNotConnectedException, 
					InvalidConnectionIdException, InvalidMarketStateException, NoSuchProductException,
					InvalidPriceOperation{
		verifyUser(userName, connId);
		Order o = new Order(userName, product, price, volume, side);
		ProductService.getInstance().submitOrder(o);
		return o.getId();
	}
	
	public void submitOrderCancel(String userName, long connId, String product, String side, String orderId)
			throws UserNotConnectedException, InvalidConnectionIdException,
			InvalidMarketStateException, NoSuchProductException{
		verifyUser(userName, connId);
		ProductService.getInstance().submitOrderCancel(product, side, orderId);
	}
	
	public void submitQuote(String userName, long connId, String product, Price bPrice, int bVolume, Price sPrice, int
			sVolume) 
					throws UserNotConnectedException, InvalidConnectionIdException, InvalidDataOperation, 
					InvalidPriceOperation, InvalidMarketStateException, NoSuchProductException{
		verifyUser(userName, connId);
		Quote q = new Quote(userName, product, bPrice, bVolume, sPrice, sVolume);
		ProductService.getInstance().submitQuote(q);
	}
	
	public void submitQuoteCancel(String userName, long connId, String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateException, 
			NoSuchProductException, InvalidDataOperation, InvalidPriceOperation{
		verifyUser(userName, connId);
		ProductService.getInstance().submitQuoteCancel(userName, product);
	}
	
	public void subscribeCurrentMarket(String userName, long connId, String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException{
		verifyUser(userName, connId);
		CurrentMarketPublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	public void subscribeLastSale(String userName, long connId, String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException{
		verifyUser(userName, connId);
		LastSalePublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
		
	public void subscribeMessages(String userName, long connId, String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException{
		verifyUser(userName, connId);
		MessagePublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	public void subscribeTicker(String userName, long connId, String product)
			throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException{
		verifyUser(userName, connId);
		TickerPublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	public void unsubscribeCurrentMarket(String userName, long connId, String product)
			throws UserNotConnectedException, InvalidConnectionIdException, NotSubscribedException{
		verifyUser(userName, connId);
		CurrentMarketPublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	public void unsubscribeLastSale(String userName, long connId, String product)
			throws UserNotConnectedException, InvalidConnectionIdException, NotSubscribedException{
		verifyUser(userName, connId);
		LastSalePublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	public void unsubscribeTicker(String userName, long connId, String product)
			throws UserNotConnectedException, InvalidConnectionIdException, NotSubscribedException{
		verifyUser(userName, connId);
		TickerPublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	public void unsubscribeMessages(String userName, long connId, String product)
			throws UserNotConnectedException, InvalidConnectionIdException, NotSubscribedException{
		verifyUser(userName, connId);
		MessagePublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
}
