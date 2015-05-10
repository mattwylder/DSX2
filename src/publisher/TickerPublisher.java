package publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import client.User;
import price.Price;

public class TickerPublisher implements Publisher {

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
		if (!publisher.getSubscriptions().containsKey(product)) {
			return;
		}
		Iterator<User> itr = publisher.getSubscriptions().get(product)
				.iterator();
		char direction = NO_PREVIOUS_VALUE;
		System.out.println("Previous value: " + lastSales.containsValue(product));
		if (lastSales.containsValue(product)) {
			Price lastSale;
			lastSale = lastSales.get(product);
			if (lastSale.greaterThan(price)) {
				direction = DECREASE;
			} else if (lastSale.lessThan(price)) {
				direction = INCREASE;
			} else {
				direction = NO_CHANGE;
			}
		}

		while (itr.hasNext()) {
			itr.next().acceptTicker(product, price, direction);
		}
		lastSales.put(product, price);
	}

	public void subscribe(User user, String product)
			throws AlreadySubscribedException {
		publisher.subscribe(user, product);

	}

	public void unSubscribe(User user, String product)
			throws NotSubscribedException {
		publisher.unSubscribe(user, product);

	}

	@Override
	public Map<String, ArrayList<User>> getSubscriptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
