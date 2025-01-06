/*
 * Copyright (c) 2017 Cumulocity GmbH, Duesseldorf, Germany and/or its affiliates and/or their licensors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.apama.samples;

import java.util.LinkedHashMap;
import java.util.Map;

import com.softwareag.connectivity.AbstractSimpleCodec;
import com.softwareag.connectivity.Message;
import com.softwareag.connectivity.PluginConstructorParameters.CodecConstructorParameters;
import com.softwareag.connectivity.util.MapExtractor;

/**
 * Converts the given event map data to the CSV format and vice versa which
 * reads CSV file and convert it into an event map.
 */
public class CSVCodec extends AbstractSimpleCodec {

	/* Output the keys/header? */
	boolean keyPrint = false;
	
	/* Skip the first keys/header line? */
	boolean getKey = false;
	
	/* List of keys read in from the config yaml file */
	String[] keys = { };

	/**
	 * Constructor, passing parameters to AbstractSimpleCodec.
	 *
	 * The config and chainId are available as members on this
	 * class for the sub-class to refer to if needed.  This class
	 * does not require or use any configuration.
	 *
	 * @param logger Is a logger object which can be used to log to the host log file.
	 * @param params see product documentation
	 */
	public CSVCodec(org.slf4j.Logger logger, CodecConstructorParameters params) throws Exception {
		super(logger, params);

		MapExtractor config = new MapExtractor(params.getConfig(), "config");
		// Find the "EventType" config from the yaml config file
		String str = config.getStringDisallowEmpty("EventType");
		config.checkNoItemsRemaining();
		
		// "EventType" is expected to be a list of keys, split on the whitespace.
		keys = str.split("\\s");
		
		for (int i = 0; i < keys.length; i++) {
			keys[i] = keys[i].substring(1);
		}
	}
	
	/**
	 * Converts the message in CSV format to event map.
	 *
	 * If this method throws then the exception will be logged and the message dropped.
	 *
	 * @param message The message, guaranteed to be non-null and have a non-null payload.  
	 * @return a map as a payload of a message, or null if it is to be discarded.
	 */
	@Override
	public Message transformMessageTowardsHost(Message message) throws Exception {
		
		// Skip the very first message as its the CSV file header
		if (!getKey) {
			getKey = true;
			return null;
		}
		
		// Extract the String payload from the message
		String msg = (String) message.getPayload();

		// Skip blank lines. It may not be null, but we might have been passed a 
		// blank line. 
		if (msg.trim().isEmpty()) {
			return null;
		}
		
		// For the purposes of demonstration, we will allow messages delimited
		// by any of ',', tabs or ';'. 
		String delimiters = "\\,|\\t|\\;";
		Object[] arr = msg.split(delimiters);
		
		// Construct a Map of field names (keys) to values
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (int i = 0; i < arr.length; i++) {
			// strip any leading/trailing whitespace off and put it in the map
			map.put(keys[i], ((String)arr[i]).trim());
		}
		
		// Set the payload of the message to an event Map and return it
		message.setPayload(map);
		return message;
	}
	
	/**
	 * Converts the message to the CSV format.
	 *
	 * If this method throws then the exception will be logged and the message dropped.
	 *
	 * @param message The message, guaranteed to be non-null and have a non-null payload.  
	 * @return the formatted message, or null if it is to be discarded.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Message transformMessageTowardsTransport(Message message) throws Exception {
		Map<String, Object> map = (Map<String, Object>) message.getPayload();
		StringBuffer buffer = new StringBuffer();
		
		// Output the keys/header in the very first message
		if (!keyPrint) {
			
			int ksize = keys.length;
			for(String s : keys){
				buffer.append(s);
				if (ksize > 1) {
					buffer.append(",");
					ksize--;
				}
			}
			String newLine = System.getProperty("line.separator");
			buffer.append(newLine);
			keyPrint = true;
		}
		
		int msize = map.size();
		for(String s : keys){
			buffer.append(map.get(s).toString());
			if (msize > 1) {
				buffer.append(",");
				msize--;
			}
		} 
		
		message.setPayload(buffer.toString());
		return message;
	}
	
	/** Identifies the version of the API this plug-in was built against. */
	public static final String CONNECTIVITY_API_VERSION = com.softwareag.connectivity.ConnectivityPlugin.CONNECTIVITY_API_VERSION;
}
