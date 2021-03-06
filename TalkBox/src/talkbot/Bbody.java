package talkbot;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import sun.audio.*;



import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.message.*;

/*class Set{
	String name;
	ArrayList<Buttons> track = new ArrayList<Buttons>();
	
}*/
public class Bbody extends JPanel implements ActionListener {
	
	
	//req
	private JPanel sp;
	private JScrollPane scroll;
	protected Configuration con;
	private ObjectInputStream in;
	public ArrayList<JButton> categories;
	//public JButton set1, set2;
	//public ArrayList<ArrayList<Buttons>> sets;//track1, track2;
	public ArrayList<ArrayList<Buttons>> tbuttons;
	private int currentset=0;
	private Boolean on, play=false;
	private Clip clip;
	private static org.apache.logging.log4j.Logger logger = LogManager.getLogger("Bbody");//Bbody.class);
	  
	public void setup(JPanel mpanel) {
		sp = new JPanel();
		sp.setMinimumSize(new Dimension(this.getWidth(), 300));
		sp.setMaximumSize(new Dimension(this.getWidth(), 300));
		sp.setBackground(new Color(255, 204, 209));
		sp.setLayout(new FlowLayout());
		categories = new ArrayList<JButton>();
	}
	
	public void sets() {
		for (int i = 0; i < con.getNumberOfAudioSets(); i++) {
			//System.out.println(i + "\there");			
			//System.out.println(con.getNumberOfAudioSets());

			JButton temp = new JButton(con.getcatNames(i));
			categories.add(temp);
		} //System.out.println(con.getNumberOfAudioSets());
		//set1 = new JButton("Phrases");
		//set2 = new JButton("Emotions");
		for (int i = 0; i < categories.size(); i++) {
			this.add(categories.get(i));
			this.categories.get(i).addActionListener(this);
		}
		//this.add(set1);
		//this.add(set2);
		//set1.addActionListener(this);
		//set2.addActionListener(this);
	}
	
	public void setbtns() {//
		tbuttons = new ArrayList<ArrayList<Buttons>>();
		//System.out.println(con.getSet1());
		on=true;
		for (int h = 0; h < con.getNumberOfAudioSets(); h++) {
			if (h==0) { //System.out.println(con.getSet1());
				ArrayList<Buttons> temp = new ArrayList<Buttons>();
				for (int i = 0; i < con.getSetAt(h); i++) {
					//System.out.println(con.getPathToImageFile(1, i+1)); System.out.println(i);
					Buttons btn = new Buttons(con.getBtnName(h+1, i+1), con.getPathToImageFile(h+1, i+1), i+1);
					btn.addActionListener(this);
					temp.add(btn);
					
					sp.add(temp.get(i));
					this.revalidate();
					this.repaint();
					//////////////////////MAYBE SURROUND WITH TRY CATCH
				}
				tbuttons.add(temp);
			} else {
				ArrayList<Buttons> temp = new ArrayList<Buttons>();
				for (int i = 0; i < con.getSetAt(h); i++) {//System.out.println(con.getPathToImageFile(2, i+1));
					Buttons btn = new Buttons(con.getBtnName(h+1, i+1), con.getPathToImageFile(h+1, i+1), i+1);
					//btn.addActionListener(this);
					temp.add(btn);
					sp.add(temp.get(i));
					temp.get(i).setVisible(false);
					//////////////////////MAYBE SURROUND WITH TRY CATCH
				}
				tbuttons.add(temp);
			}
		}
		this.revalidate(); this.repaint();
			
	}
	
	
	public Bbody (JPanel mpanel) {
		super();
		//Read in serialized file
		try {
			
			in = new ObjectInputStream(new FileInputStream("serial/log.bin"));
			con = (Configuration) in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		this.setMinimumSize(new Dimension(mpanel.getWidth()-50, 400));
		this.setMaximumSize(new Dimension(mpanel.getWidth()-50, 400));
		this.setBackground(new Color (0, 12, 25));
		this.setLayout(new FlowLayout());
				
		this.setup(mpanel);
		this.sets();
		//Set scroll features
		scroll = new JScrollPane(sp);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(mpanel.getWidth()-100, 270));
		//scroll.setMinimumSize(new Dimension(400, this.getHeight()));
		//scroll.setMaximumSize(new Dimension(400, this.getHeight()));
		//sp.add(scroll);		
			this.setbtns();
			this.add(scroll);

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().getClass().equals(talkbot.Buttons.class)) {
			// int bn;
			/*if (on)
				bn=1;
			else
				bn=2; */
			Buttons temp = (Buttons) e.getSource(); 
			//bn = temp.getbtnNumber();
			
			String tempath = this.con.getPathToAudioFile(currentset+1, temp.getbtnNumber());
			playSound(tempath);
			logger.info("Set " + currentset + " - Button " + temp.getbtnNumber());
		} else if (categories.contains((JButton) e.getSource())) {
			if (currentset != categories.indexOf((JButton) e.getSource())) {
			for (int i = 0; i < tbuttons.get(currentset).size(); i++) {
				sp.remove(tbuttons.get(currentset).get(i));
			}
			
			currentset= categories.indexOf((JButton) e.getSource());
			//System.out.println(currentset); 
			if (!tbuttons.get(currentset).isEmpty()) {
			for (int i = 0; i < tbuttons.get(currentset).size(); i++) {
				sp.add(tbuttons.get(currentset).get(i));
				tbuttons.get(currentset).get(i).addActionListener(this);
				tbuttons.get(currentset).get(i).setVisible(true);
			}}
			this.revalidate(); this.repaint(); }
			logger.info("Set - " + currentset);
		} 
	}
	 
	public void playSound(String temp) {
	    try {
	       // Clip clip = AudioSystem.getClip();
	        
	        if (play==false) {
		        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(temp));
	        	clip = AudioSystem.getClip();
	        	clip.open(audioInputStream);
	        	clip.start();
	        	play=true;
	        }
	        else {
	        	clip.stop();
		        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(temp));
	        	clip = AudioSystem.getClip();
	        	clip.open(audioInputStream);
	        	clip.start();
	        }
	    } catch(Exception e) {
	       // System.out.println("Error with sound.");
	        e.printStackTrace();
	    }
	}

}