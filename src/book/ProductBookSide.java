package book;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import message.CancelMessage;
import message.FillMessage;
import message.InvalidDataOperation;
import price.InvalidPriceOperation;
import price.Price;
import price.PriceFactory;
import publisher.MessagePublisher;
import tradable.BookSide;
import tradable.Tradable;
import tradable.TradableDTO;

public class ProductBookSide {
	private String side;
	private HashMap<Price, ArrayList<Tradable>> bookEntries;
	private TradeProcessor processor;
	private ProductBook parent;
	
	public ProductBookSide(ProductBook pb){
		parent = pb;
		processor = new TradeProcessorPriceTimeImpl(this);
		bookEntries = new HashMap<Price, ArrayList<Tradable>>();
	}
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName){
		ArrayList<TradableDTO> list = new ArrayList<TradableDTO>();

		ArrayList<ArrayList<Tradable>> orders = new ArrayList<ArrayList<Tradable>>(bookEntries.values());
		ArrayList<Tradable> tradables;
		Tradable curTrade;
		for ( int i = 0; i < orders.size(); i++){
			tradables = orders.get(i);
			for(int j = 0; j < tradables.size(); j++){
				curTrade = tradables.get(j);
				if(curTrade.getUser().equals(userName) && curTrade.getRemainingVolume() > 0){
					list.add(new TradableDTO(curTrade.getProduct(), curTrade.getPrice(), curTrade.getOriginalVolume(),
							curTrade.getRemainingVolume(),curTrade.getCancelledVolume(), curTrade.getUser(), 
							curTrade.getSide(), curTrade.isQuote(), curTrade.getId()));
				}
			}
		}
		return list;
	}

	protected synchronized ArrayList<Tradable> getEntriesAtTopOfBook(){
		if(bookEntries.isEmpty()){
			return null;
		}
		ArrayList<Price> sorted = getSortedPrices();
		if (side == BookSide.BUY) {
			Collections.reverse(sorted); // Reverse them
		}
		
		return bookEntries.get(sorted.get(0));
	}
	
	public synchronized String[] getBookDepth(){
		if(bookEntries.isEmpty()){
			String[] empty = new String[1];
			empty[0] = "<Empty>";
			return empty;
		}
		String[] depth = new String[bookEntries.size()];

		ArrayList<Price> sorted = getSortedPrices();
		ArrayList<Tradable> tradables;
		Tradable curTrade;
		int vol = 0;
		for(int i = 0; i < sorted.size(); i++){
			tradables = bookEntries.get(sorted.get(i));
			for(int j = 0; j < tradables.size(); j++){
				curTrade = tradables.get(j);
				vol += curTrade.getRemainingVolume();
			}
			depth[i] = sorted.get(i) + " x " + vol;
		}
		
		return depth;
	}
	
	protected synchronized ArrayList<Tradable> getEntriesAtPrice(Price price){
		return bookEntries.get(price);
	}
	
	public synchronized boolean hasMarketPrice(){
		if(bookEntries.containsKey(PriceFactory.makeMarketPrice())){
			return true;
		}
		return false;
	}
	
	public synchronized boolean hasOnlyMarketPrice(){
		if(bookEntries.size() == 1 && bookEntries.containsKey(PriceFactory.makeMarketPrice())){
			return true;
		}
		return false;
	}
	
	public synchronized Price topOfBookPrice(){
		if(bookEntries.isEmpty()){
			return null;
		}
		ArrayList<Price> sorted = getSortedPrices();
		return sorted.get(0);
	}
	
	public synchronized int topOfBookVolume(){
		if(bookEntries.isEmpty()){
			return 0;
		}
		ArrayList<Price> sorted = getSortedPrices();
		Price best = sorted.get(0);
		ArrayList<Tradable> tradables = bookEntries.get(best);
		int vol = 0;
		for(int i = 0; i < tradables.size(); i++){
			vol += tradables.get(i).getRemainingVolume();
		}
		return vol;
	}
	
	public synchronized boolean isEmpty(){
		return bookEntries.isEmpty();
	}
	
	public synchronized void cancelAll(){
		ArrayList<ArrayList<Tradable>> orders = new ArrayList<ArrayList<Tradable>>(bookEntries.values());
		ArrayList<Tradable> tradables;
		for( int i = 0; i < orders.size(); i++){
			tradables = orders.get(i);
			for( int j = 0; j < tradables.size(); j++ ){
				submitOrderCancel(tradables.get(j).getId());
				submitQuoteCancel(tradables.get(j).getUser());
			}
		}
	}
	
	public synchronized TradableDTO removeQuote(String user){
		ArrayList<ArrayList<Tradable>> orders = new ArrayList<ArrayList<Tradable>>(bookEntries.values());
//		This method should search the book (the “bookEntries” HashMap) for a Quote from the specified user (a user
//				can only have one Quote per product so there will either be none, or one). Once found, remove the Quote
//				from the book, and create a TradableDTO using data from that QuoteSide, and return the DTO from the
//				method. If no quote is found, return null. Note, if the Quote was the last Tradable in the ArrayList of Tradables
//				at that price, remove the price entry from the “bookEntries” HashMap (i.e., bookEntries.remove(price) )
		for(ArrayList<Tradable> lst : orders){
			for(Tradable trd : lst){
				if(trd.getUser().equals(user)){
					lst.remove(trd);
					TradableDTO result = new TradableDTO(trd.getProduct(), trd.getPrice(), trd.getOriginalVolume(),
							trd.getRemainingVolume(), trd.getCancelledVolume(), trd.getUser(), trd.getSide(), 
							trd.isQuote(), trd.getId());
					if(lst.isEmpty()){
						bookEntries.remove(trd.getPrice());
					}
					return result;
				}
			}
		}

		return null;
	}
	
	public synchronized void submitOrderCancel(String orderId){
		ArrayList<ArrayList<Tradable>> orders = new ArrayList<ArrayList<Tradable>>(bookEntries.values());
		ArrayList<Tradable> tradables;
		Tradable curTradable;
		for(int i = 0; i < orders.size(); i++){
			tradables = orders.get(i);
			for(int j = 0; j < tradables.size(); j++){
				curTradable = tradables.get(j);
				if(curTradable.getId().equals(orderId)){
					tradables.remove(j);
					MessagePublisher pub = MessagePublisher.getInstance();
					try{
					pub.publishCancel(new CancelMessage(curTradable.getUser(), curTradable.getProduct() ,curTradable.getPrice(),
							curTradable.getRemainingVolume(), "Cancelled from ProductBookSide", curTradable.getSide(),
							curTradable.getId()));
					} catch (InvalidDataOperation e){
						System.out.println(e);
					}
					addOldEntry(curTradable);
					if(tradables.isEmpty()){
						orders.remove(i);
					}
				}
			}
		}
	}
	
	public synchronized void submitQuoteCancel(String userName){
		TradableDTO quote = removeQuote(userName);
		if(quote != null){
			try {
				MessagePublisher.getInstance().publishCancel(new CancelMessage(quote.getUser(),
						quote.getProduct(), quote.getPrice(), quote.getRemainingVolume(),
						"Quote " + quote.side + "-Side cancelled", quote.getSide(), quote.getId()));
			} catch (InvalidDataOperation e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addOldEntry(Tradable t){
		parent.addOldEntry(t);
	}
	
	public synchronized void addToBook(Tradable trd){
		if(bookEntries.containsKey(trd.getPrice())){
			bookEntries.get(trd.getPrice()).add(trd);
		}
		else{
			ArrayList<Tradable> tmp = new ArrayList<Tradable>();
			tmp.add(trd);
			bookEntries.put(trd.getPrice(), tmp);
		}
	}
	
	public HashMap<String, FillMessage> tryTrade(Tradable trd) 
			throws InvalidDataOperation, InvalidPriceOperation{
		HashMap<String, FillMessage> allFills;
		if(trd.getSide().equals("BUY")){
			allFills = trySellAgainstBuySideTrade(trd);
		}
		else{
			allFills = tryBuyAgainstSellSideTrade(trd);
		}
		ArrayList<FillMessage> fills = new ArrayList<FillMessage>(allFills.values());
		MessagePublisher pub = MessagePublisher.getInstance();
		for(FillMessage mes : fills){
			pub.publishFill(mes);
		}
		return allFills;
	}
	
	public synchronized HashMap<String,FillMessage> tryBuyAgainstSellSideTrade(Tradable trd) 
			throws InvalidDataOperation, InvalidPriceOperation{
		HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> fillMsgs = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> someMsgs;
		boolean canTrade = trd.getRemainingVolume() > 0 && !isEmpty() &&
				(trd.getPrice().lessOrEqual(topOfBookPrice()) ||trd.getPrice().isMarket() );
		
		while(canTrade){
			someMsgs = processor.doTrade(trd);
			fillMsgs = mergeFills(fillMsgs, someMsgs);
			canTrade = (trd.getRemainingVolume() > 0 && !isEmpty()) &&
					(trd.getPrice().lessOrEqual(topOfBookPrice()) ||trd.getPrice().isMarket() );
		}
		allFills.putAll(fillMsgs);
		return allFills;
	}
	
	public synchronized HashMap<String,FillMessage> trySellAgainstBuySideTrade(Tradable trd) 
			throws InvalidDataOperation, InvalidPriceOperation{
		HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> fillMsgs = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> someMsgs;

		boolean canTrade = (trd.getRemainingVolume() > 0 && !isEmpty() && trd.getPrice().greaterOrEqual(topOfBookPrice()))
				|| (trd.getRemainingVolume() > 0 && !bookEntries.isEmpty() && trd.getPrice().isMarket());
		while(canTrade){
			someMsgs = processor.doTrade(trd);
			fillMsgs = mergeFills(fillMsgs, someMsgs);
			canTrade= (trd.getRemainingVolume() > 0 && !bookEntries.isEmpty()) && 
					(trd.getPrice().greaterOrEqual(topOfBookPrice()) || trd.getPrice().isMarket());
			
		}
		allFills.putAll(fillMsgs);
		return allFills;
	}
	
	private HashMap<String, FillMessage> mergeFills(HashMap<String, FillMessage> existing, HashMap<String,
			FillMessage> newOnes){
		if(existing.isEmpty()){
			return new HashMap<String, FillMessage>(newOnes);
		}
		HashMap<String,FillMessage> results = new HashMap<>(existing);
		for (String key : newOnes.keySet()) { // For each Trade Id key in the “newOnes” HashMap
			if (!existing.containsKey(key)) { // If the “existing” HashMap does not have that key...
				results.put(key, newOnes.get(key)); // ...then simply add this entry to the “results” HashMap
			} else { // Otherwise, the “existing” HashMap does have that key – we need to update the data
				FillMessage fm = results.get(key); // Get the FillMessage from the “results” HashMap
				try {
					fm.setVolume(newOnes.get(key).getVolume());
				} catch (InvalidDataOperation e) {
					e.printStackTrace();
				} // Update the fill volume
				fm.setDetails(newOnes.get(key).getDetails()); // Update the fill details
			}
		}
		return results;
	}
	
	public synchronized void clearIfEmpty(Price p){
		if(bookEntries.containsKey(p) && bookEntries.get(p).isEmpty()){
			bookEntries.remove(p);
		}
	}
	
	public synchronized void removeTradable(Tradable t){
		ArrayList<Tradable> entries = bookEntries.get(t.getPrice());
		if(entries == null){
			return;
		}
		if(!entries.remove(t)){
			return;
		}
		if(entries.isEmpty()){
			clearIfEmpty(t.getPrice());
		}
	}
	
	private ArrayList<Price> getSortedPrices(){
		ArrayList<Price> sorted = new ArrayList<Price>(bookEntries.keySet()); // Get prices
		Collections.sort(sorted); // Sort them
		return sorted;
	}
}