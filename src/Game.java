import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Game implements KeyListener {
	private long timeIntervalSeconds;
	private long refreshRateSeconds;
	private long nextRefreshMillis;
	private HashMap<Integer, Drink> offeredDrinks;
	private LinkedList<Drink> drinksAsList;
	private GUI gui;
	private GUI gui2;
	private long startingTimeMillis;
	private Algorithm algorithm;
	public final static double DEBUG_TIME_FACTOR = 1.0; // Set 1 when not in debug
	// mode

	// In Abhängigkeit von Standardpreis
	double upPerSell = 0.10;
	double downPerInterval = 0.60;
	
	public enum Algorithm {
    GAUSSIAN, EFUNCTION, SIMPLE
	}



	public Game(GUI gui, GUI gui2, long timeinterval, long refreshRate) {
		startingTimeMillis = System.currentTimeMillis();
		this.gui = gui;
		this.gui2 = gui2;
		loadSettings();
		offeredDrinks = new HashMap<Integer, Drink>();
		drinksAsList= new LinkedList<Drink>();
		refreshRateSeconds = refreshRate;
		timeIntervalSeconds = timeinterval;
		nextRefreshMillis = startingTimeMillis + refreshRate * 1000;
		algorithm=Algorithm.SIMPLE;
	}

	private double div(double a, double b) {
		return a / (b + Double.MIN_VALUE);
	}

	private double round(double price) {
		double result;
		long tmp = Math.round(price * 10.0);
		result = tmp / 10.0;

		return result;

	}

	private double getPriceSchlumpf(Drink d) {
		Random r = new Random();
		double gaussian = r.nextGaussian()*Math.min(d.getStandardPrice()-d.getMinPrice(), d.getMaxPrice()-d.getStandardPrice());
		double price= d.getStandardPrice()+gaussian;
		if (price > d.getMaxPrice())
			price = d.getMaxPrice();
		else if (price < d.getMinPrice())
			price = d.getMinPrice();
		
		return price;
		
	}
	
	private double getPriceSimple(Drink d) {
		double plus = d.numberOfSoldItems(timeIntervalSeconds) * upPerSell * d.getStandardPrice();		
		double down = downPerInterval;
		long passedSeconds = (long)((System.currentTimeMillis()-startingTimeMillis)/1000);
		if(passedSeconds<timeIntervalSeconds) {
			down/=(double)timeIntervalSeconds/(double)passedSeconds;
		}
		double minus = DEBUG_TIME_FACTOR * down * d.getStandardPrice();

		//System.out.println("minus: "+minus+"   plus: "+plus+" down: "+down);
		double price = d.getStandardPrice() + plus - minus;
		if (price > d.getMaxPrice())
			price = d.getMaxPrice();
		else if (price < d.getMinPrice())
			price = d.getMinPrice();

		return round(price);
	}

	private double getPriceEFunction(Drink d) {
		long passedMillis = (System.currentTimeMillis() - startingTimeMillis);
		double passedHours = passedMillis / 1000.0 / 60.0 / 60.0
				* DEBUG_TIME_FACTOR;
		double price;
		double averageSoldPerHour;

		if (d.getItemsSoldCount() > 0 && passedHours > 1.0) {
			averageSoldPerHour = div(d.getItemsSoldCount(), passedHours);
			double timeintervalSoldPerHour = div(
					d.numberOfSoldItems(timeIntervalSeconds),
					timeIntervalSeconds / 3600.0 * DEBUG_TIME_FACTOR);
			System.out.println(timeIntervalSeconds / 3600.0 * DEBUG_TIME_FACTOR
					+ " " + timeintervalSoldPerHour + " " + averageSoldPerHour);
			double p = div(timeintervalSoldPerHour, averageSoldPerHour);
			double a = (d.getStandardPrice() - d.getMinPrice())
					/ (Math.E - 1.0);
			double b = d.getMinPrice()
					- (d.getStandardPrice() - d.getMinPrice()) / (Math.E - 1.0);
			price = a * Math.pow(Math.E, p) + b;

			if (price > d.getMaxPrice()) {
				price = d.getMaxPrice();
			}
		} else {
			price = d.getStandardPrice();
		}

		return price;
	}

	public void loadSettings() {
		// timeIntervalSeconds = 10;
	}

	public void addDrink(Drink d) {
		offeredDrinks.put(d.getHotkey(), d);
		drinksAsList.add(d);
		gui.addDrink(d);
		gui2.addDrink(d);

		int progress = (int) (100 - 100.0 * (nextRefreshMillis - System.currentTimeMillis())
				/ (refreshRateSeconds * 1000.0));
		gui.show(drinksAsList, progress, true);
		gui2.show(drinksAsList, progress, true);
	}

	public void saveSettings() {

	}

	/**
	 * 
	 * @param interval
	 *            in seconds
	 */
	public void setTimeInterval(long interval) {
		timeIntervalSeconds = interval;
	}

	public void update() {

		long currentTimeMillis = System.currentTimeMillis();
		boolean refresh;
		int progress;

		int tooExpensive = 0;
		if (nextRefreshMillis < currentTimeMillis) {
			refresh = true;
			nextRefreshMillis += refreshRateSeconds * 1000;
			for (Drink d : offeredDrinks.values()) {
				double price = 0.0;
				switch(algorithm) {
				case GAUSSIAN:
					price = getPriceSchlumpf(d);
					break;
					
				case EFUNCTION:
					price = getPriceEFunction(d);
					break;

				case SIMPLE:
					price = getPriceSimple(d);
				default:
						
					
				}
				d.setCurrentPrice(price);
				if (price > d.getStandardPrice()) {
					tooExpensive += 1;
				} else if (price < d.getStandardPrice()) {
					tooExpensive -= 1;
				}
			}

			if (tooExpensive > 0) {
				downPerInterval += downPerInterval*0.1;
			} else if (tooExpensive < 0) {
				downPerInterval -= downPerInterval*0.1;
			}
		} else
			refresh = false;

		progress = (int) (100 - 100.0 * (nextRefreshMillis - currentTimeMillis)
				/ (refreshRateSeconds * 1000.0));
		//gui.show(offeredDrinks.values(), progress, refresh);
		gui.show(drinksAsList, progress, refresh);
		gui2.show(drinksAsList, progress, refresh);
	}

	public void close() {
		gui.dispose();
		gui2.dispose();
		System.exit(0);
	}

	public void writeLogfile() {
		LogManager lm = LogManager.getLogManager();
		FileHandler fh;
		try {
			fh = new FileHandler("BoersenpartyLog"+System.currentTimeMillis()+".txt");
			Logger namesLogger = Logger.getLogger("NamesLogger");
			Logger timestampLogger = Logger.getLogger("TimestampLogger");
			
			lm.addLogger(namesLogger);
			lm.addLogger(timestampLogger);
			namesLogger.setLevel(Level.INFO);
			timestampLogger.setLevel(Level.INFO);
			//fh.setFormatter(new XMLFormatter());

			timestampLogger.addHandler(fh);
			namesLogger.addHandler(fh);
			for (Drink d : offeredDrinks.values()) {
				namesLogger.log(Level.INFO, d.getName());
				for (Long timestamp : d.getLog()) {
					timestampLogger.log(Level.INFO, "" + timestamp);
				}
			}

			fh.close();
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

	}

	public void toggleGaussianAlg() {
		if(algorithm.equals(Algorithm.SIMPLE)) {
			algorithm= Algorithm.GAUSSIAN;
			gui.setProgressBarColor(Color.GREEN);
			gui2.setProgressBarColor(Color.GREEN);
		} else {
			algorithm= Algorithm.SIMPLE;
			gui.setProgressBarColor(Color.YELLOW);
			gui2.setProgressBarColor(Color.YELLOW);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		/*
		 * if (e.getKeyChar() == KeyEvent.VK_ESCAPE) close(); else
		 */if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			 gui.toggleProgressBar();
			 gui2.toggleProgressBar();
			 
		 }
		 else if(e.getKeyChar() == KeyEvent.VK_L) {
			 writeLogfile();
		 } else if(e.getKeyChar() == KeyEvent.VK_ENTER) {
			 toggleGaussianAlg();
		 }
		Drink d = offeredDrinks.get(e.getKeyCode());
		if (d != null) {
			d.sellOneItem();
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
