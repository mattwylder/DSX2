package message;

public class MarketMessage {

	private String state;

	public MarketMessage(String st) throws InvalidDataOperation {

		if (st.equals("CLOSED") || st.equals("OPEN") || st.equals("PREOPEN")) {
			state = st;
		} else {
			throw new InvalidDataOperation(
					"Market is not either Open, Closed, or Preopen");
		}
	}

	public String getState() {
		return state;
	}

	public String toString() {
		return "The Market is " + state;
	}

}
