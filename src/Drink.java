import java.util.LinkedList;

public class Drink {
	private String name;
	private double minPrice;
	private double maxPrice;
	private double standardPrice;
	private double currentPrice;
	private int hotkey;
	private LinkedList<Long> sellingTimesSeconds;
	private int tendency;

	/**
	 * 
	 * @param name
	 * @param minPrice
	 * @param maxPrice
	 * @param standardPrice
	 * @param c
	 */
	public Drink(String name, double minPrice, double maxPrice,
			double standardPrice, int hotkey) {
		super();
		this.name = name;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.standardPrice = standardPrice;
		this.currentPrice = standardPrice;
		this.hotkey = hotkey;
		sellingTimesSeconds = new LinkedList<Long>();
		tendency = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}

	public double getStandardPrice() {
		return standardPrice;
	}

	public void setStandardPrice(double standardPrice) {
		this.standardPrice = standardPrice;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		if (currentPrice > this.currentPrice) {
			tendency = 1;
		} else if (currentPrice == this.currentPrice) {
			tendency = 0;
		} else {
			tendency = -1;
		}
		this.currentPrice = currentPrice;
	}

	public int getHotkey() {
		return hotkey;
	}

	public void setHotkey(char hotkey) {
		this.hotkey = hotkey;
	}

	public int getItemsSoldCount() {
		return sellingTimesSeconds.size();
	}

	public void sellOneItem() {
		sellingTimesSeconds.addFirst(System.currentTimeMillis() / 1000);
	}

	public void resetSellingHistory() {
		sellingTimesSeconds.clear();
	}

	/**
	 * Number of sold Items in the last timeinterval seconds
	 * 
	 * @param timeinterval
	 *            in seconds
	 * @return
	 */
	public int numberOfSoldItems(long timeinterval) {
		int counter = 0;
		double threshold = System.currentTimeMillis() / 1000.0 - timeinterval;

		for (Long time : sellingTimesSeconds) {
			if (time > threshold) {
				counter++;
			} else {
				break;
			}
		}

		return counter;
	}

	public int getTendency() {

		return tendency;
	}

	public double getMaxPrice() {
		return maxPrice;
	}
	
	public LinkedList<Long> getLog() {
		return sellingTimesSeconds;
	}

}
