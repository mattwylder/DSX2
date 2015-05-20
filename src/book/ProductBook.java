package book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import GlobalConstants.MarketState;
import price.InvalidPriceOperation;
import price.Price;
import price.PriceFactory;
import publisher.CurrentMarketPublisher;
import publisher.LastSalePublisher;
import publisher.MarketDataDTO;
import publisher.MessagePublisher;
import tradable.*;
import message.*;


public class ProductBook {
	private String product;
	private ProductBookSide sellSide;
	private ProductBookSide buySide;
	private String lastCurrentMarket;
	private HashSet<String> userQuotes = new HashSet<>();
	private HashMap<Price, ArrayList<Tradable>> oldEntries = new HashMap<Price, ArrayList<Tradable>>();
	
	public ProductBook(String stockSymbol){
		product = stockSymbol;
		buySide = new ProductBookSide(this, "BUY");
		sellSide = new ProductBookSide(this, "SELL");
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName){
		ArrayList<TradableDTO> results = new ArrayList<TradableDTO>();
		results.addAll(buySide.getOrdersWithRemainingQty(userName));
		results.addAll(sellSide.getOrdersWithRemainingQty(userName));
		return results;
	}
	
	public synchronized void checkTooLateToCancel(String orderId) 
			throws OrderNotFoundException{
		ArrayList<ArrayList<Tradable>> old = new ArrayList<ArrayList<Tradable>>(oldEntries.values());
		ArrayList<Tradable> tradables;
		Tradable curTradable;
		boolean found = false;
		for(int i = 0; i < old.size() ; i++){
			tradables = old.get(i);
			for(int j = 0; i < tradables.size(); j++){
				curTradable = tradables.get(j);
				if(curTradable.getId().equals(orderId)){
					found = true;
					try{
						MessagePublisher.getInstance().publishCancel(new CancelMessage(curTradable.getUser(), curTradable.getProduct() ,curTradable.getPrice(),
								curTradable.getRemainingVolume(), "Too late to cancel", curTradable.getSide(),
								curTradable.getId()));
					} catch(InvalidDataOperation e){
						System.out.println(e);
					}
				}
			}
		}
		if(!found){
			throw new OrderNotFoundException("Order " + orderId + " not found");
		}
	}
	
	public synchronized String[ ][ ] getBookDepth(){
		String[][] bd = new String[2][];
		bd[0] = buySide.getBookDepth();
		bd[1] = sellSide.getBookDepth();
		return bd;
	}
	
	public synchronized MarketDataDTO getMarketData(){
		Price buyPrice = buySide.topOfBookPrice();
		Price sellPrice = sellSide.topOfBookPrice();
		int buyVol = buySide.topOfBookVolume();
		int sellVol = sellSide.topOfBookVolume();
		if(buyPrice == null){
			buyPrice = PriceFactory.makeLimitPrice(0);
		}
		if(sellPrice == null){
			sellPrice = PriceFactory.makeLimitPrice(0);
		}
		return new MarketDataDTO(product, buyPrice, buyVol, sellPrice, sellVol);
	}

	
	public synchronized void addOldEntry(Tradable t){
		if(!oldEntries.containsKey(t.getPrice())){
			ArrayList<Tradable> tmp = new ArrayList<Tradable>();
			tmp.add(t);
			oldEntries.put(t.getPrice(), tmp);
		}
		try {
			t.setCancelledVolume(t.getRemainingVolume());
			t.setRemainingVolume(0);
		} catch (InvalidPriceOperation e) {
			System.out.println(e);
			e.printStackTrace();
		}
		oldEntries.get(t.getPrice()).add(t);
		
	}
	
	public synchronized void openMarket() 
			throws InvalidDataOperation, InvalidPriceOperation{
		Price buyPrice = buySide.topOfBookPrice();
		Price sellPrice = sellSide.topOfBookPrice();
		ArrayList<Tradable> topOfBuySide = null;
		HashMap<String, FillMessage> allFills = null;
		ArrayList<Tradable> toRemove = null;
		if(buyPrice == null || sellPrice == null){
			return;
		}
		while(buyPrice.greaterOrEqual(sellPrice) || buyPrice.isMarket() || sellPrice.isMarket()){
			topOfBuySide = buySide.getEntriesAtPrice(buyPrice);
			for(Tradable t : topOfBuySide){
				allFills = sellSide.tryTrade(t);
				if(t.getRemainingVolume() == 0){
					toRemove.add(t);
				}
			}
			for(Tradable t : toRemove){
				buySide.removeTradable(t);
			}
			updateCurrentMarket();
			Price lastSalePrice = determineLastSalePrice(allFills);
			int lastSaleVolume = determineLastSaleQuantity(allFills);
			LastSalePublisher.getInstance().publishLastSale(product, lastSalePrice, lastSaleVolume);
			buyPrice = buySide.topOfBookPrice();
			sellPrice = sellSide.topOfBookPrice();
			if(buyPrice == null || sellPrice == null){
				break;
			}
		}
	}
	
	public synchronized void closeMarket(){
		buySide.cancelAll();
		sellSide.cancelAll();
		updateCurrentMarket();
	}
	
	public synchronized void cancelOrder(String side, String orderId){
		if(side.equals("BUY")){
			buySide.submitOrderCancel(orderId);
		}
		else{
			sellSide.submitOrderCancel(orderId);
		}
		updateCurrentMarket();
	}
	
	public synchronized void cancelQuote(String userName){
		buySide.submitQuoteCancel(userName);
		sellSide.submitQuoteCancel(userName);
		updateCurrentMarket();
	}
	
	public synchronized void addToBook(Quote q) 
				throws DataValidationException, InvalidDataOperation, InvalidPriceOperation{
		QuoteSide buyQuote = q.getQuoteSide("BUY");
		QuoteSide sellQuote = q.getQuoteSide("SELL");
		Price buyPrice = q.getQuoteSide("BUY").getPrice();
		Price sellPrice = q.getQuoteSide("SELL").getPrice();
		if(sellPrice.lessOrEqual(buyPrice)){
			throw new DataValidationException("That is an illegal Quote - Sell price must be greater than Buy price.");
		}
		if(buyPrice.lessOrEqual(PriceFactory.makeLimitPrice(0))){
			throw new DataValidationException("That is an illegal Quote - Buy price must be greater than zero."); 
		}
		if(sellPrice.lessOrEqual(PriceFactory.makeLimitPrice(0))){
			throw new DataValidationException("That is an illegal Quote - Sell price must be greater than zero."); 
		}
		if(buyQuote.getOriginalVolume() <= 0 || sellQuote.getOriginalVolume() <= 0){
			throw new DataValidationException("That is an illegal Quote - Volume must be greater than zero."); 
		}
		if (userQuotes.contains(q.getUserName())){
			buySide.removeQuote(q.getUserName());
			sellSide.removeQuote(q.getUserName());
			updateCurrentMarket();
		}
		addToBook("BUY", buyQuote);
		addToBook("SELL", sellQuote);
		userQuotes.add(q.getUserName());
		updateCurrentMarket();
	}
	
	public synchronized void addToBook(Order o) 
			throws InvalidDataOperation, InvalidPriceOperation{
		addToBook(o.getSide(), o);
		updateCurrentMarket();
	}
	
	public synchronized void updateCurrentMarket(){
		String mkt = "" + buySide.topOfBookPrice() + buySide.topOfBookVolume() +
				sellSide.topOfBookPrice() + sellSide.topOfBookVolume();
		if(lastCurrentMarket == null){
			lastCurrentMarket = mkt;
		}
		else if(!lastCurrentMarket.equals(mkt)){
			MarketDataDTO dto = new MarketDataDTO(product, buySide.topOfBookPrice(), buySide.topOfBookVolume(),
					sellSide.topOfBookPrice(), sellSide.topOfBookVolume());
			CurrentMarketPublisher.getInstance().publishCurrentMarket(dto);
			lastCurrentMarket = mkt;
		}
	}
	
	private synchronized Price determineLastSalePrice(HashMap<String, FillMessage> fills){
		ArrayList<FillMessage> msgs = new ArrayList<>(fills.values());
		Collections.sort(msgs);
		return msgs.get(0).getPrice();
	}
	
	private synchronized int determineLastSaleQuantity(HashMap<String, FillMessage> fills){
		ArrayList<FillMessage> msgs = new ArrayList<>(fills.values());
		Collections.sort(msgs);
		return msgs.get(0).getVolume();
	}
	
	private synchronized void addToBook(String side, Tradable trd) 
			throws InvalidDataOperation, InvalidPriceOperation{
		if(ProductService.getInstance().getMarketState().equals(MarketState.PREOPEN)){
			if(side.equals("BUY")){
				buySide.addToBook(trd);
			}
			else{
				sellSide.addToBook(trd);
			}
			return;
		}
		HashMap<String, FillMessage> allFills = null;
		
		if(side.equals("BUY")){
			allFills = sellSide.tryTrade(trd);
		}
		else{
			allFills = buySide.tryTrade(trd);
		}
		if(allFills != null && !allFills.isEmpty()){
			updateCurrentMarket();
			int volDifference = trd.getOriginalVolume() - trd.getRemainingVolume();
			Price lastSalePrice = determineLastSalePrice(allFills);
			LastSalePublisher.getInstance().publishLastSale(product, lastSalePrice, volDifference);
		}
		if(trd.getRemainingVolume() > 0){
			if(trd.getPrice().isMarket()){
				try {
					CancelMessage c = new CancelMessage(trd.getUser(), product,
							trd.getPrice(), trd.getRemainingVolume(),
							"Cancelled", trd.getSide(), trd.getId());
					MessagePublisher.getInstance().publishCancel(c);
				} catch (InvalidDataOperation e) {
					System.out.println(e);
					e.printStackTrace();
				}
			}
			else if(trd.getSide().equals("BUY")){
				buySide.addToBook(trd);
			}
			else{
				sellSide.addToBook(trd);
			}
		}
	}
}
