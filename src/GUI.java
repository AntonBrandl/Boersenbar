import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;


public class GUI extends JFrame {
	JPanel table;
	JProgressBar progressBar;
	ArrayList<JLabel> elements;
	Color progressBarColor;
	public GUI(String title) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
		table = new JPanel();
		table.setLayout(new BoxLayout(table, BoxLayout.Y_AXIS));
		table.setBackground(Color.BLACK);
		progressBar = new JProgressBar(1, 100);
		progressBar.setValue(0);
		progressBar.setForeground(Color.yellow);
		progressBar.setBackground(Color.black);
		progressBar.setBorderPainted(false);
		table.add(progressBar);
		add(table);
		elements = new ArrayList<JLabel>();
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setVisible(true);
		progressBarColor = Color.yellow;
	}

	public void refresh() {

	}

	public void show(Collection<Drink> collection, int progress, boolean refreshPrices) {
		int count = 0;
		for (Drink d : collection) {
			elements.get(count*3+0).setText(d.getName());
			if(refreshPrices) {
				String priceText = new Double(String.format("%10.1f", d.getCurrentPrice())).toString().replace(".", ",")+"0 €";
				String tendencyChar;
				if (d.getTendency() == 1){
				//if(d.getCurrentPrice()>d.getStandardPrice()) {
					tendencyChar = "\u2191";
					elements.get(count*3+2).setForeground(Color.RED);
				}
				else if (d.getTendency() == 0){

				//else if(d.getCurrentPrice()==d.getStandardPrice()) {
					tendencyChar = "\u2192";
					elements.get(count*3+2).setForeground(Color.YELLOW);
				}
				else {
					tendencyChar = "\u2193";
					elements.get(count*3+2).setForeground(Color.GREEN);	
				}
				elements.get(count*3+1).setText(priceText);
				elements.get(count*3+2).setText(tendencyChar);
			}
			float alpha = 1.0f-(progress+40)/100.0f;
			alpha*=2.0f;
			if(alpha>1.0f)
				alpha=1.0f;
			else if(alpha<0.0f) 
				alpha=0.0f;
			float r = progressBarColor.getRed()/256f;
			float g = progressBarColor.getGreen()/256f;
			float b = progressBarColor.getBlue()/256f;
			progressBar.setForeground(new Color(r, g,b,alpha));
			count++;
		}
		 progressBar.setValue(progress);
	}

	public void showConsole(Collection<Drink> collection) {
		System.out.flush();
		System.out.println("Drinks:n");
		System.out.println("Name\tPrice\t\t\tTendency\taverage\tsoldf\tsolda");
		for (Drink d : collection) {
			String name = d.getName();
			double price = d.getCurrentPrice();
			int tendency = d.getTendency();
			String tendencyChar;
			if (tendency == 1)
				tendencyChar = "\u2191";
			else if (tendency == 0)
				tendencyChar = "\u2192";
			else
				tendencyChar = "\u2193";
			int soldTotal = d.getItemsSoldCount();
			int soldFrame = d.numberOfSoldItems(60);
			double average = soldFrame / (soldTotal + Double.MIN_VALUE);
			System.out.println(name + "\t" + price + "\t" + tendencyChar
					+ "\t\t" + average + "\t" + soldFrame + "\t" + soldTotal
					+ "\n");
		}
	}

	public void addDrink(Drink d) {
		JPanel row = new JPanel();
		JLabel namelabel = new JLabel();
		JLabel pricelabel = new JLabel();
		JLabel tendencyLabel = new JLabel();
		namelabel.setForeground(Color.WHITE);
		pricelabel.setForeground(Color.WHITE);
		tendencyLabel.setForeground(Color.WHITE);

		int fontsize=40;
		namelabel.setFont(new Font("Century Schoolbook L", Font.BOLD, fontsize));
		pricelabel.setFont(new Font("Century Schoolbook L", Font.BOLD, fontsize));
		tendencyLabel.setFont(new Font("Arial", Font.BOLD, fontsize));

		elements.add(namelabel);
		elements.add(pricelabel);
		elements.add(tendencyLabel);
		row.setBackground(Color.BLACK);
		row.add(namelabel);
		row.add(pricelabel);
		row.add(tendencyLabel);
//		namelabel.setBorder(new EmptyBorder(5, 10, 5, 10) );
//		pricelabel.setBorder(new EmptyBorder(5, 2, 5, 2) );
//		tendencyLabel.setBorder(new EmptyBorder(5, 10, 5, 10) );
//		
		namelabel.setPreferredSize(new Dimension(800,50));
		pricelabel.setPreferredSize(new Dimension(200,50));
		table.add(row);
	}

	public void toggleProgressBar() {
		progressBar.setVisible(!progressBar.isVisible());
	}
	
	public void setProgressBarColor(Color c) {
		progressBarColor=c;
	}
}