package publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import client.User;

public class PublisherImpl implements Publisher {

	protected Map<String, ArrayList<User>> subscriptions;

	protected PublisherImpl() {
		subscriptions = new HashMap<String, ArrayList<User>>();
	}

	public Map<String, ArrayList<User>> getSubscriptions() {
		return subscriptions;
	}

	public synchronized void subscribe(User u, String product)
			throws AlreadySubscribedException {
		if (subscriptions.containsKey(product)) {
			ArrayList<User> subscribers = subscriptions.get(product);
			Iterator<User> itr = subscribers.iterator();
			while (itr.hasNext()) {
				if (itr.next().equals(u)) {
					throw new AlreadySubscribedException("User " + u
							+ " already subscribed");
				}
			}
			subscribers.add(u);
		} else {
			subscriptions.put(product, new ArrayList<User>());
			subscriptions.get(product).add(u);
		}
	}

	public synchronized void unSubscribe(User u, String product)
			throws NotSubscribedException {
		ArrayList<User> subscribers = subscriptions.get(product);
		User tmp;
		int i = 0;
		while (i < subscribers.size()) {
			if (subscribers.get(i).equals(u)) {
				subscribers.remove(i);
				return;
			}
			i++;
		}
		throw new NotSubscribedException("User " + u + " not subscribed.");
	}

}
