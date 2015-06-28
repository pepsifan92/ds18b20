/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ds18b20control.internal;

import java.io.IOException;
import java.util.TreeMap;

import org.openhab.binding.ds18b20control.ds18b20controlBindingProvider;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.NumberItem;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;


/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author MichaelP
 * @since 1.0
 */
public class ds18b20controlGenericBindingProvider extends AbstractGenericBindingProvider implements ds18b20controlBindingProvider {

	private static final Logger logger = 
			LoggerFactory.getLogger(ds18b20controlGenericBindingProvider.class);
	
	I2CBus 	bus;
	private TreeMap<Integer, I2CDevice> ds18b20Map = new TreeMap<>();
	
	public ds18b20controlGenericBindingProvider() {
		 try {
			bus = I2CFactory.getInstance(I2CBus.BUS_1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getBindingType() {
		return "ds18b20control";
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		if (!(item instanceof NumberItem)) {
			throw new BindingConfigParseException("item '" + item.getName()
					+ "' is of type '" + item.getClass().getSimpleName()
					+ "', only NumberItems are allowed - please check your *.items configuration");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {		
		super.processBindingConfiguration(context, item, bindingConfig);
			
		String[] properties = bindingConfig.split(";");		
		ds18b20controlConfig config = new ds18b20controlConfig();
		try{
			
			config.address = Integer.parseInt(properties[0]);
			config.pinNumber = Integer.parseInt(properties[1]);
			
			checkOfValidValues(config, item.getName());
			addBindingConfig(item, config);	
			handleBoards(config);
				
			logger.debug("processBindingConfiguration: (ds18b20control) ItemName: {}, Addresses: {}", item.toString(), ds18b20Map.keySet());
		}catch(Exception e){
			e.printStackTrace();
		}						
	}
	
	/* ================================= SELF WRITTEN METHODS - BEGIN ===============================*/
	
	private void checkOfValidValues(ds18b20controlConfig config, String itemName){
		if(config.address < 72 || config.address > 80 ){
			throw new IllegalArgumentException("The given address '" + config.address + "'of the item '" + itemName + "' is invalid! " +
					"PCA8591 must be 72-80 (0x48-0x50)");
		}
		
		if(config.pinNumber < 0 || config.pinNumber > 3){
			throw new IllegalArgumentException("The pinNumber of the item '" + itemName + "'is invalid! Must be 0-3.");
		}				
	}
		
	private void handleBoards(ds18b20controlConfig config){
		try {
			if(!ds18b20Map.containsKey(config.address)){
				try{
					I2CDevice device = bus.getDevice(config.address);
					ds18b20Map.put(config.address, device);	
					logger.debug("handleBoards: added ds18b20 board with address: {} !", config.address);
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
			removeUnusedBoardsFromMap(config);
		} catch (Exception e) {
			e.printStackTrace();
			//logger.debug("Exception in ds18b20 handleBoards... however, it works.");
		}
	}
	
	private void removeUnusedBoardsFromMap(ds18b20controlConfig config){
		keyLoop:
		for(Integer mapKey : ds18b20Map.keySet()){
			logger.debug("handleBoards: mapKey {} !", mapKey);
			for(BindingConfig bindingConfig : bindingConfigs.values()){
				ds18b20controlConfig conf = (ds18b20controlConfig) bindingConfig;				
				logger.debug("handleBoards: check {} !", conf.address);
				if(mapKey == conf.address){
					logger.debug("removeUnusedBoardsFromMap: board found with address: {} !", conf.address);
					continue keyLoop;
				}				
			}
			if(!bindingConfigs.values().isEmpty()){
				ds18b20Map.remove(mapKey);
				logger.debug("removeUnusedBoardsFromMap: removed board with address: {} !", mapKey);
			}
		}
	}
	
	/* ----> The following methods are getter and setter to grant access of the data of this BindingConfiguration. 
	 * They implement/override the methods of the interface "ds18b20controlBindingProvider.java".   
	 * The access of them from "ds18b20controlBinding.java" is possible by using the collection "providers" which is 
	 * inherit from "AbstractBinding.java". 
	 * Thats the way to communicate and get data from the BindingConfig.
	 */
	
	@Override
	public int getAddress(String itemName) {
		ds18b20controlConfig config = (ds18b20controlConfig) bindingConfigs.get(itemName);
		
		if (config == null) {
			throw new IllegalArgumentException("The item name '" + itemName + "'is invalid or the item isn't configured");
		}
		
		return config.address;
	}

	@Override
	public int getPinNumber(String itemName) {
		ds18b20controlConfig config = (ds18b20controlConfig) bindingConfigs.get(itemName);
		
		if (config == null) {
			throw new IllegalArgumentException("The item name '" + itemName + "'is invalid or the item isn't configured");
		}
		
		return config.pinNumber;
	}	
	
	@Override
	public boolean isItemConfigured(String itemName) {
		if (bindingConfigs.containsKey(itemName)) {
			return true;
		}
		return false;
	}
	
	@Override
	public TreeMap<Integer, I2CDevice> getds18b20Map() {		
		return ds18b20Map;
	}

	/* ================================= SELF WRITTEN METHODS - END ===============================*/
	
	
	/* ------------------------ Binding config class ----------------------- */
	/* This will be used to create a object in processBindingConfiguration
	 * and add it as Binding configuration (addBindingConfig()) for each item
	 * which was configured in the *.items file in the openHAB configuration.  
	 */
	public class ds18b20controlConfig implements BindingConfig{
		int address;
		int pinNumber;
	}

}
