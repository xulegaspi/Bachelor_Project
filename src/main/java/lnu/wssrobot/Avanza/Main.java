/**
 * Main.java
 * 27 feb 2016
 */
package lnu.wssrobot.Avanza;

import lnu.wssrobot.FakeJsonConsumer;
import lnu.wssrobot.JsonObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A simple driver class setting up a stream (BlockingQueue) between a
 * producer thread (DNCrawler) and a consumer (FakeJsonConsumer).
 * 
 * @author jlnmsi
 *
 */
public class Main {


	public static void main(String[] args) {
		BlockingQueue<JsonObject> producer2consumer = new LinkedBlockingQueue<JsonObject>();
		
		FakeJsonConsumer consumer = new FakeJsonConsumer(producer2consumer);
		consumer.start();
		
		AvanzaCrawler producer = new AvanzaCrawler(producer2consumer);
		producer.start();
		

	}

}
