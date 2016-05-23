import java.awt.GraphicsEnvironment;
import java.awt.event.*;

public class Main {
	public static void main(String[] args) {
		GUI gui = new GUI("Börsenparty");
		GUI gui2 = new GUI("Börsenparty");
		Game game = new Game(gui, gui2, 45*60, 2*60);//5*60);

		game.addDrink(new Drink("Pi\u00F1a Colada", 3.5, 8.0, 5.0, KeyEvent.VK_F1));
		game.addDrink(new Drink("Long-Island Icetea", 4, 9.0, 5.5, KeyEvent.VK_F2));
		game.addDrink(new Drink("Caipirinha / Mojito", 3.5, 8.0, 5.0, KeyEvent.VK_F3));
		game.addDrink(new Drink("Touchdown / Tequila Sunrise", 3.5, 8.0, 5.0, KeyEvent.VK_F4));
		game.addDrink(new Drink("Sex on the Beach", 3.5, 8.0, 5.0, KeyEvent.VK_F5));
		game.addDrink(new Drink("Singapore Sling", 3.5, 8.0, 5.0, KeyEvent.VK_F6));
		game.addDrink(new Drink("Bacardi Razz / Cuba Libre", 3.5, 6.5, 5.0, KeyEvent.VK_F7));
		game.addDrink(new Drink("Gin Tonic / Wodka Bull / Martini", 3.5, 6.5, 3.3, KeyEvent.VK_F8));
		game.addDrink(new Drink("White Russian", 2.8, 6.5, 2.8, KeyEvent.VK_F9));
		game.addDrink(new Drink("Shot", 1, 2.5, 1.2, KeyEvent.VK_F10));
		game.addDrink(new Drink("B52", 1.5, 3.2, 2.0, KeyEvent.VK_F11));
		
		gui.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});

		gui2.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});

		gui.addKeyListener(game);
		gui2.addKeyListener(game);
		
		
		boolean run = true;
		while (run) {
			game.update();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
