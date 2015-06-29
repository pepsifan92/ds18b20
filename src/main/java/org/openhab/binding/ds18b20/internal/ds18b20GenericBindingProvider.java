/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ds18b20.internal;

import java.io.IOException;
import java.util.TreeMap;

import org.openhab.binding.ds18b20.ds18b20BindingProvider;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.NumberItem;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;


/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author MichaelP
 * @since 1.0
 */
public class ds18b20GenericBindingProvider extends AbstractGenericBindingProvider implements ds18b20BindingProvider {

	private static final Logger logger = 
			LoggerFactory.getLogger(ds18b20GenericBindingProvider.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getBindingType() {
		return "ds18b20";
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
		
		try{
			Temperature temperature = new Temperature(bindingConfig); //Set deviceId
			addBindingConfig(item, temperature);
				
//			logger.debug("=================== processBindingConfiguration: (ds18b20) ItemName: {}", item.toString());
		}catch(Exception e){
			e.printStackTrace();
		}						
	}	

	@Override
	public Temperature getTemperature(String itemName) {		
		Temperature temperature = (Temperature) bindingConfigs.get(itemName);
		
		if (temperature == null) {
			throw new IllegalArgumentException("The item name '" + itemName + "'is invalid or the item isn't configured");
		}
		
		return temperature;
	}

}
