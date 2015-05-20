package publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import client.User;
import price.Price;

public class TickerPublisher extends PublisherImpl {

	private static final char INCREASE = (char) 8593;
	private static final char DECREASE = (char) 8595;
	private static final char NO_CHANGE = '=';
	private static final char NO_PREVIOUS_VALUE = ' ';

	private static Publisher publisher;
	private static volatile TickerPublisher instance;
	Map<String, Price> lastSales;

	protected TickerPublisher() {
		super();
		lastSales = new HashMap<String, Price>();
		publisher = PublisherFactory.makePublisher();
	}

	public static TickerPublisher getInstance() {
		if (instance == null) {
			synchronized (TickerPublisher.class) {
				if (instance == null)
					instance = new TickerPublisher();
			}
		}
		return instance;
	}

	public synchronized void publishTicker(String product, Price price) {
		Price lastSale = lastSales.get(product);
		if (!subscriptions.containsKey(product)) {
			return;
		}
		Iterator<User> itr = subscriptions.get(product).iterator();
		char direction = NO_PREVIOUS_VALUE;	
		
		if (lastSale == null){
			lastSales.put(product, price);
		}
		else if (lastSale.greaterThan(price)) {
			direction = DECREASE;
			lastSales.replace(product, price);
		} else if (lastSale.lessThan(price)) {
			direction = INCREASE;
			lastSales.replace(product, price);
		} else {
			direction = NO_CHANGE;
		}
	
		while (itr.hasNext()) {
			itr.next().acceptTicker(product, price, direction);
		}
		
	}

}
