/**
 * JsonObject.java
 * 27 feb 2016
 */
package lnu.wssrobot;

import org.codehaus.jackson.JsonNode;

/**
 * A simple interface defining common properties of 
 * all Json objects streamed from producer to consumer. 
 * 
 * @author jlnmsi
 *
 */
public interface JsonObject {

	public String toJsonString();
	
	public JsonNode toJsonNode();
}
