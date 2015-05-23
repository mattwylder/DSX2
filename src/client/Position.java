package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import price.InvalidPriceOperation;
import price.Price;
import price.PriceFactory;

public class Position {
	private HashMap<String, Integer> holdings;
	private Price costs;
	private HashMap<String, Price> lastSales;
	
	public Position(){
		holdings = new HashMap<String, Integer>();
		lastSales = new HashMap<String, Price>();
		costs = PriceFactory.makeLimitPrice(0);
	}
	
	public void updatePosition(String product, Price price, String side, int volume) throws InvalidPriceOperation{
		int adjusted;
		if(side.equals("BUY")){
			adjusted = volume;
		}
		else{
			adjusted = -1 * volume;
		}
		if(!holdings.containsKey(product)){
			holdings.put(product, adjusted);
		}
		else{
			int curVol = holdings.get(product);
			int newVol = curVol + adjusted;
			if(newVol == 0){
				holdings.remove(product);
			}
			else{
				holdings.put(product, newVol);
			}
		}
		Price totalPrice = price.multiply(volume);
		if(side.equals("BUY")){
			costs = costs.subtract(totalPrice);
		}
		else{
			costs = costs.add(totalPrice);
		}
	}
	
	public void updateLastSale(String product, Price price){
		lastSales.put(product, price);
	}
	
	public int getStockPositionVolume(String product){
		if(!holdings.containsKey(product)){
			return 0;
		}
		return holdings.get(product);
	}
	
	public ArrayList<String> getHoldings(){
		ArrayList<String> h = new ArrayList<>(holdings.keySet());
		Collections.sort(h);
		return h;
	}
	
	public Price getStockPositionValue(String product) throws InvalidPriceOperation{
		if(!holdings.containsKey(product)){
			return PriceFactory.makeLimitPrice("0");
		}
		Price last = lastSales.get(product);
		if(last == null){
			return PriceFactory.makeLimitPrice("0");
		}
		return last.multiply(holdings.get(product));
	}
	
	public Price getAccountCosts(){
		return costs;
	}
	
	public Price getAllStockValue() throws InvalidPriceOperation{
		ArrayList<String> products = new ArrayList<String>(holdings.keySet());
		Iterator<String> itr = products.iterator();
		Price tmp = PriceFactory.makeLimitPrice(0);
		while(itr.hasNext()){
			tmp.add(getStockPositionValue(itr.next()));
		}
		return tmp;
	}
	
	public Price getNetAccountValue() throws InvalidPriceOperation{
		return getAllStockValue().add(costs);
	}
}
