/**
 * Util.java
 * 28 feb 2016
 */
package lnu.wssrobot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * @author jlnmsi
 *
 */
public class Util {
	
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	public static String getCurrentTime() {
		Date date = new Date();
		String dateTime = timeFormat.format( date);
		return dateTime;
	}
	
	private static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static String getCurrentDay() {
		Date date = new Date();
		String dateTime = dayFormat.format( date);
		return dateTime;
	}
	
	public static void addToQueue(BlockingQueue<JsonObject> jsonQueue, JsonObject json) {
		try {  jsonQueue.put( json ); } 
		catch (InterruptedException ex) { ex.printStackTrace();}
	}
	
	public static void sleep(int ms) {
		try { Thread.sleep(ms);	   
		} catch (InterruptedException e) { e.printStackTrace();}  
	}
}
