import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class PlayNoticeSound implements Runnable{
	
	public PlayNoticeSound(){
		
		Thread t = new Thread(this);
		t.start();
		
	}
	
	private void playSound() throws Exception{
		
			//File a = new File("/home/oneday-cafe/바탕화면/Oneday Cafe OrderList Program Beta Version/Notice.wav");	// Ubuntu Environment
			File a = new File("Notice.wav");
			AudioInputStream b = AudioSystem.getAudioInputStream(a);
			//Clip c = AudioSystem.getClip(null);																	// Ubuntu Environment
			Clip c = AudioSystem.getClip();
			
			c.open(b);
			c.start();
				
			
			Thread.sleep(c.getMicrosecondLength()/1000);

	}
	
	public void run(){
		try{
			playSound();	
		}
		catch(Exception e){
			
			
		}
	}
}
