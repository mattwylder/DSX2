package price;

import java.util.HashMap;
import java.util.Map;

public class PriceFactory {

	private static Map<String, Price> prices = new HashMap<String, Price>();
	private static Price market = new MarketPrice();

	public static Price makeLimitPrice(String value) {
		
		if(!value.contains(".")){
			value = value +".00";
		}

		value = value.replaceAll("[$]", "");

		if (prices.containsKey(value)) {
			return prices.get(value);
		} else {
			Price p = new Price((long) (Double.parseDouble(value) * 100));
			prices.put(value, p);
			return p;
		}
	}

	public static Price makeLimitPrice(long value) {

		long cents = Math.abs(value % 100);
		String centVal;
		if (cents < 10) {
			centVal = "0" + cents;
		} else {
			centVal = "" + cents;
		}
		String val = String.format("%d.%s", value / 100, centVal);

		return makeLimitPrice(val);

	}

	public static Price makeMarketPrice() {
		return market;
	}

}
