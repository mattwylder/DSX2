package tradable;

import message.InvalidDataOperation;
import price.InvalidPriceOperation;
import price.Price;

public class Quote {
	
	private static final String BUY_SIDE = "BUY";
	private static final String SELL_SIDE = "SELL";
	
	private String user;
	private String stockSymbol;
	private QuoteSide buy;
	private QuoteSide sell;
//	private long time = System.nanoTime();
	private Price price;
	private int originalVolume;
//	private int remainingVolume;
	private int cancelledVolume;
	private String id;
	

	public Quote(String userName, String productSymbol,
			Price buyPrice, int buyVolume,
			Price sellPrice, int sellVolume) throws InvalidDataOperation, InvalidPriceOperation{
		if(buyVolume < 1 || sellVolume < 1){
			throw new InvalidPriceOperation("Cannot have 0 or negative buy volume");
		}
		setUser(userName);
		setStockSymbol(productSymbol);
		buy = new QuoteSide(userName, productSymbol, buyPrice, buyVolume, BUY_SIDE);
		sell = new QuoteSide(userName, productSymbol, sellPrice, sellVolume, SELL_SIDE);
	}
	
	private void setUser(String userIn) 
			throws InvalidDataOperation {
		if(userIn == null || userIn == null){
			throw new InvalidDataOperation("Username is null or the empty string");
		}
		user = userIn;
	}
	
	private void setStockSymbol(String stockSymbolIn)
			throws InvalidDataOperation {
		if(stockSymbolIn == null || stockSymbolIn.isEmpty()){
			throw new InvalidDataOperation("Stock Symbol is null or the empty string");
		}
		stockSymbol = stockSymbolIn;
	}
	
	private void setBuy(){
		
	}
	
	private void setSell(){
		
	}
	
	public String getUserName(){
		return user;
	}
	
	public String getProduct(){
		return stockSymbol;
	}
	
	public QuoteSide getQuoteSide(String sideln){
		if(sideln.equals("BUY")){
			return buy;
		}
		else{
			return sell;
		}
	}
	
	public String toString(){
		return user + "order : BUY " + buy + stockSymbol + " at " + price + "(Original vol: " + originalVolume + "CXL vol: " + cancelledVolume + "_), ID: " + id;
	}
}