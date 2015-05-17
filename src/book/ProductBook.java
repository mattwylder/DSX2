package book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import price.Price;
import publisher.MarketDataDTO;
import tradable.*;
import message.*;


public class ProductBook {
	private String product;
	private ProductBookSide sellSide;
	private ProductBookSide buySide;
	private String marketDataValues;
	private HashSet<String> userQuotes = new HashSet<>();
	private HashMap<Price, ArrayList<Tradable>> oldEntries = new HashMap<Price, ArrayList<Tradable>>();
	
	public ProductBook(String stockSymbol){
		product = stockSymbol;
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName){
		return null;
	}
	
	public synchronized void checkTooLateToCancel(String orderId){
		
	}
	
	public synchronized String[ ][ ] getBookDepth(){
		return null;
	}
	
	public synchronized MarketDataDTO getMarketData(){
		return null;
	}
	
	public synchronized void addOldEntry(Tradable t){
		
	}
	
	public synchronized void openMarket(){
		
	}
	
	public synchronized void closeMarket(){
		
	}
	
	public synchronized void cancelOrder(BookSide side, String orderId){
		
	}
	
	public synchronized void cancelQuote(String userName){
		
	}
	
	public synchronized void addToBook(Quote q){
		
	}
	
	public synchronized void addToBook(Order o){
		
	}
	
	public synchronized void updateCurrentMarket(){
		
	}
	
	private synchronized Price determineLastSalePrice(HashMap<String, FillMessage> fills){
		return null;
	}
	
	private synchronized int determineLastSaleQuantity(HashMap<String, FillMessage> fills){
		return 0;
	}
	
	private synchronized void addToBook(BookSide side, Tradable trd){
		
	}
}
