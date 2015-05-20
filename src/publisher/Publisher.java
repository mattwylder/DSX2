package publisher;

import java.util.ArrayList;
import java.util.Map;

import client.User;

public interface Publisher {
	
	public void subscribe(User user, String product) throws AlreadySubscribedException;
	public void unSubscribe(User user, String product) throws NotSubscribedException;
	//public Map<String, ArrayList<User>> getSubscriptions(); //TODO: Get rid of this
}
