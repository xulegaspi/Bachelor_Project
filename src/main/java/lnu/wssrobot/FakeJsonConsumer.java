/**
 * FakeJsonConsumer.java
 * 27 feb 2016
 */
package lnu.wssrobot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

/**
 * A fake Json consumer just saving all Json objects read from a queue 
 * in an ordinary text file.
 * 
 * @author jlnmsi
 *
 */
public class FakeJsonConsumer extends Thread {
		
	private final BlockingQueue<JsonObject> jsonQueue;
	
	public FakeJsonConsumer( BlockingQueue<JsonObject> queue) {
		super("FakeClientThread");
		jsonQueue = queue;
		
		System.out.println("Enter "+this.getClass().getName());
	}
	
	@Override
	public void run() {
		System.out.println("FakeJsonConsumer thread up and running ...");

		PrintWriter out = null;
		try {

			// Open output file
			String dumpPath = getFakeConsumerDumpDir();
			String dumpFile = dumpPath + File.separator + "jsonDump.txt";
			out = new PrintWriter(new FileWriter(dumpFile));

			try {
				while (true) { // Save json object 
					// Read from input queue
					JsonObject obj = jsonQueue.take(); 
					//System.out.println(obj.toJsonString());
					out.println(Util.getCurrentTime()+"\t"+obj.toString());
					//System.out.println(obj.toJsonString());
					out.flush();

				}
			} catch (InterruptedException ex) { ex.printStackTrace();}
		}catch (IOException e) {
			System.err.println(e);
		}
		finally{
			if(out != null){
				out.close();
			}
		}
	}
	
	private static String dump_path = null;
	public static String getFakeConsumerDumpDir() {
		if (dump_path == null) {
			String user_path = System.getProperty("user.dir");
			dump_path = user_path + File.separator + "fakeConsumer_dump";
			File file = new File(dump_path);
			if (!file.exists())
				file.mkdirs();
		}
		System.out.println("Fake dump dir: "+dump_path);
		return dump_path;
	}
}
