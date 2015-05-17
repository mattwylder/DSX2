package book;

import java.util.ArrayList;
import java.util.HashMap;

import GlobalConstants.MarketState;
import publisher.MarketDataDTO;
import tradable.*;

public class ProductService {
	
	private ProductService instance;
	private HashMap<String, ProductBook> books;
	private String marketState;
	
	private ProductService(){
		marketState = MarketState.CLOSED;
	}
	
	public ProductService getInstance(){
		if(instance == null){
			//TODO:synchronize
			instance = new ProductService();
		}
		return instance;
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemaining(String userName, String product){
		return null;
	}
	
	public synchronized MarketDataDTO getMarketData(String product){
		return null;
	}
	
	public synchronized MarketState getMarketState(){
		return marketState;
	}
	
	public synchronized String[][] getBookDepth(String product){
		return null;
	}
	
	public synchronized ArrayList<String> getProductList(){
		return null;
	}
	
	public synchronized void setMarketState(String ms){
		marketState = ms;
	}
	
	public synchronized void createProduct(String product){
		
	}
	
	public synchronized void submitQuote(Quote q){
		
	}
	
	public synchronized String submitOrder(Order o){
		return null;
	}
	
	public synchronized void submitOrderCancel(String product, BookSide side, String orderid){
		
	}
	
	public synchronized void submitQuoteCancel(String userName, String product){
		
	}
	
}
