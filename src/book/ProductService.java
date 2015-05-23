package book;

import java.util.ArrayList;
import java.util.HashMap;

import message.InvalidDataOperation;
import message.MarketMessage;
import GlobalConstants.MarketState;
import price.InvalidPriceOperation;
import publisher.CurrentMarketPublisher;
import publisher.MarketDataDTO;
import publisher.MessagePublisher;
import tradable.*;

public class ProductService {
	
	private static ProductService instance;
	private HashMap<String, ProductBook> allBooks;
	private String marketState;
	
	private ProductService(){
		marketState = "CLOSED";
		allBooks = new HashMap<String,ProductBook>();
	}
	
	public static ProductService getInstance(){
		if(instance == null){
			synchronized(ProductService.class){
				if(instance == null){
					instance = new ProductService();
				}
			}
		}
		return instance;
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName, String product) throws InvalidDataOperation, InvalidPriceOperation{
		return allBooks.get(product).getOrdersWithRemainingQty(userName);
	}
	
	public synchronized MarketDataDTO getMarketData(String product){
		return allBooks.get(product).getMarketData();
	}
	
	public synchronized String getMarketState(){
		return marketState;
	}
	
	public synchronized String[][] getBookDepth(String product)
			throws NoSuchProductException{
		if(!allBooks.containsKey(product)){
			throw new NoSuchProductException("Product " + product + " does not exist");
		}
		return allBooks.get(product).getBookDepth();
	}
	
	public synchronized ArrayList<String> getProductList(){
		return new ArrayList<String>(allBooks.keySet());	
	}
	
	public synchronized void setMarketState(String ms)
			throws InvalidMarketStateTransition, InvalidDataOperation, InvalidPriceOperation{
		if(marketState.equals("CLOSED") && ms.equals("OPEN")){
			throw new InvalidMarketStateTransition("Market cannot transition directly from CLOSED to OPEN");
		}
		else if(marketState.equals("PREOPEN") && ms.equals("CLOSED")){
			throw new InvalidMarketStateTransition("Market cannot transition directly from PREOPEN to CLOSED");
		}
		else if(marketState.equals("OPEN") && ms.equals("PREOPEN")){
			throw new InvalidMarketStateTransition("Market cannot transition from OPEN to PREOPEN");
		}
		marketState = ms;
		MessagePublisher.getInstance().publishMarketMessage(new MarketMessage(ms));
		for(ProductBook book : allBooks.values()){
			if(marketState.equals("OPEN")){
				book.openMarket();
			}
			else if(marketState.equals("CLOSED")){
				book.closeMarket();
			}
		}
	}
	
	public synchronized void createProduct(String product)
			throws DataValidationException, ProductAlreadyExistsException{
		if(product == null || product.isEmpty()){
			throw new DataValidationException("Null or empty product name");
		}
		if(allBooks.containsKey(product)){
			throw new ProductAlreadyExistsException("Product " + product + " already exists");
		}
		allBooks.put(product, new ProductBook(product));
	}
	
	public synchronized void submitQuote(Quote q)
			throws InvalidMarketStateException, NoSuchProductException{
		if(marketState.equals("CLOSED")){
			throw new InvalidMarketStateException("Market is CLOSED, cannot submit order");
		}
		if(!allBooks.containsKey(q.getProduct())){
			throw new NoSuchProductException("Product " + q.getProduct() + " does not exist. Order cannot be submited");
		}
		try {
			allBooks.get(q.getProduct()).addToBook(q);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized String submitOrder(Order o)
			throws InvalidMarketStateException, NoSuchProductException, InvalidDataOperation, InvalidPriceOperation{
		if(marketState.equals("CLOSED")){
			throw new InvalidMarketStateException("Market is CLOSED, cannot submit order");
		}
		if(marketState.equals("PREOPEN") && o.getPrice().isMarket()){
			throw new InvalidMarketStateException("Market is PREOPEN, cannot submit Market orders.");
		}
		if(!allBooks.containsKey(o.getProduct())){
			throw new NoSuchProductException("Product " + o.getProduct() + " does not exist. Order cannot be submited");
		}
		allBooks.get(o.getProduct()).addToBook(o);
		return o.getId();
	}
	
	public synchronized void submitOrderCancel(String product, String side, String orderId)
			throws InvalidMarketStateException, NoSuchProductException{
		if(marketState.equals("CLOSED")){
			throw new InvalidMarketStateException("Market is CLOSED, cannot submit order");
		}
		if(!allBooks.containsKey(product)){
			throw new NoSuchProductException("Product " + product + " does not exist. Order cannot be submited");
		}
		allBooks.get(product).cancelOrder(side, orderId);
	}
	
	public synchronized void submitQuoteCancel(String userName, String product)
			throws InvalidMarketStateException, NoSuchProductException, InvalidDataOperation, InvalidPriceOperation{
		if(marketState.equals("CLOSED")){
			throw new InvalidMarketStateException("Market is CLOSED, cannot submit order");
		}
		if(!allBooks.containsKey(product)){
			throw new NoSuchProductException("Product " + product + " does not exist. Order cannot be submited");
		}
		allBooks.get(product).cancelQuote(userName);
	}
	
}
