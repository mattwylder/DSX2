package client;

import gui.UserDisplayManager;

import java.util.ArrayList;

import message.CancelMessage;
import message.FillMessage;
import message.InvalidDataOperation;
import price.InvalidPriceOperation;
import price.Price;
import publisher.AlreadySubscribedException;
import tradable.TradableDTO;

import java.sql.Timestamp;

import book.InvalidMarketStateException;
import book.NoSuchProductException;

public class UserImpl implements User{
	
	private String userName;
	private long connId;
	private ArrayList<String> stocks;
	private ArrayList<TradableUserData> ordersSubmitted;
	private Position position;
	private UserDisplayManager manager;

	public UserImpl(String userIn) throws InvalidDataOperation {
		setUser(userIn);
		ordersSubmitted = new ArrayList<TradableUserData>();
		position = new Position();
		//manager = new UserDisplayManager(this);
	}

	@Override
	public String getUserName() {
		return userName;
	}
	
	private void setUser(String userIn)
			throws InvalidDataOperation {
		if(userIn == null || userIn.isEmpty()){
			throw new InvalidDataOperation("User is null or the empty string");
		}
		userName = userIn;
	}

	@Override
	public void acceptLastSale(String product, Price p, int v) {
		manager.updateLastSale(product, p, v);
		position.updateLastSale(product, p);
	}

	@Override
	public void acceptMessage(FillMessage fm) {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		String summary = stamp.toString() + "Fill Message: " + fm.getSide() + 
				fm.getProduct() + " at " + fm.getPrice() + fm.getDetails() + "[Tradable ID: " + fm.getId() + "]";
		manager.updateMarketActivity(summary);
		try {
			position.updatePosition(fm.getProduct(), fm.getPrice(), fm.getSide(), fm.getVolume());
		} catch (InvalidPriceOperation e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	@Override
	public void acceptMessage(CancelMessage cm) {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		String summary = stamp.toString() + "Fill Message: " + cm.getSide() + 
				cm.getProduct() + " at " + cm.getPrice() + cm.getDetails() + "[Tradable ID: " + cm.getId() + "]";
		manager.updateMarketActivity(summary);
		try {
			position.updatePosition(cm.getProduct(), cm.getPrice(), cm.getSide(), cm.getVolume());
		} catch (InvalidPriceOperation e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	@Override
	public void acceptTicker(String product, Price p, char direction) {
		manager.updateTicker(product, p, direction);
	}

	@Override
	public void acceptCurrentMarket(String product, Price bp, int bv, Price sp,
			int sv) {
		manager.updateMarketData(product, bp, bv, sp, sv);
	}

	@Override
	public void acceptMarketMessage(String message) {
		manager.updateMarketState(message);
	}

	@Override
	public void connect() 
			throws AlreadyConnectedException, UserNotConnectedException, InvalidConnectionIdException {
		connId = UserCommandService.getInstance().connect(this);
		stocks = UserCommandService.getInstance().getProducts(userName, connId);
	}

	@Override
	public void disconnect() throws UserNotConnectedException, InvalidConnectionIdException {
		UserCommandService.getInstance().disconnect(userName, connId);		
	}

	@Override
	public void showMarketDisplay() throws Exception {
		if(stocks == null){
			throw new UserNotConnectedException("User not connected");
		}
		if(manager == null){
			manager = new UserDisplayManager(this);
		}
		manager.showMarketDisplay();
	}

	@Override
	public String submitOrder(String product, Price price, int volume, String side)
			throws InvalidDataOperation, UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateException, NoSuchProductException, InvalidPriceOperation {
		String id = UserCommandService.getInstance().submitOrder(userName, connId, product, price, volume, side);
		TradableUserData tud = new TradableUserData(userName, product, side, id);
		ordersSubmitted.add(tud);
		return id;
	}

	@Override
	public void submitOrderCancel(String product, String side, String orderId) 
			throws UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateException, NoSuchProductException {
		UserCommandService.getInstance().submitOrderCancel(userName, connId, product, side, orderId);		
	}

	@Override
	public void submitQuote(String product, Price buyPrice, int buyVolume,
			Price sellPrice, int sellVolume) 
					throws UserNotConnectedException, InvalidConnectionIdException, InvalidDataOperation, InvalidPriceOperation, InvalidMarketStateException, NoSuchProductException {
		UserCommandService.getInstance().submitQuote(userName, connId, product, buyPrice, buyVolume, sellPrice, sellVolume);
	}

	@Override
	public void submitQuoteCancel(String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateException, NoSuchProductException, InvalidDataOperation, InvalidPriceOperation {
		UserCommandService.getInstance().submitQuoteCancel(userName, connId, product);	
	}

	@Override
	public void subscribeCurrentMarket(String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException {
		UserCommandService.getInstance().subscribeCurrentMarket(userName, connId, product);
	}

	@Override
	public void subscribeLastSale(String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException {
		UserCommandService.getInstance().subscribeLastSale(userName, connId, product);
	}

	@Override
	public void subscribeMessages(String product)
			throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException {
		UserCommandService.getInstance().subscribeMessages(userName, connId, product);
	}

	@Override
	public void subscribeTicker(String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, AlreadySubscribedException {
		UserCommandService.getInstance().subscribeTicker(userName, connId, product);
	}

	@Override
	public Price getAllStockValue() throws InvalidPriceOperation {
		//FIXME: Maybe we shouldn't be propagating this
		return position.getAllStockValue();
	}

	@Override
	public Price getAccountCosts() {
		return position.getAccountCosts();
	}

	@Override
	public Price getNetAccountValue() throws InvalidPriceOperation {
		//FIXME: Maybe we shouldn't be propagating this
		return position.getNetAccountValue();
	}

	@Override
	public String[][] getBookDepth(String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException {
		return UserCommandService.getInstance().getBookDepth(userName, connId, product);
	}

	@Override
	public String getMarketState() throws UserNotConnectedException, InvalidConnectionIdException {
		return UserCommandService.getInstance().getMarketState(userName, connId);
	}

	@Override
	public ArrayList<TradableUserData> getOrderIds() {
		return ordersSubmitted;
	}

	@Override
	public ArrayList<String> getProductList() {
		return stocks;
	}

	@Override
	public Price getStockPositionValue(String sym) throws InvalidPriceOperation {
		// FIXME maybe don't propagate
		return position.getStockPositionValue(sym);
	}

	@Override
	public int getStockPositionVolume(String product) {
		return position.getStockPositionVolume(product);
	}

	@Override
	public ArrayList<String> getHoldings() {
		return position.getHoldings();
	}

	@Override
	public ArrayList<TradableDTO> getOrdersWithRemainingQty(String product) 
			throws UserNotConnectedException, InvalidConnectionIdException, InvalidDataOperation, InvalidPriceOperation {
		// FIXME maybe don't propagate		
		return UserCommandService.getInstance().getOrdersWithRemainingQty(userName, connId, product);
	}

}
