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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.lang.StringUtils;
import org.openhab.binding.ds18b20.ds18b20BindingProvider;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement this class if you are going create an actively polling service
 * like querying a Website/Device.
 * 
 * @author MichaelP
 * @since 1.0
 */
public class ds18b20Binding extends AbstractActiveBinding<ds18b20BindingProvider> {

	private static final Logger logger = 
		LoggerFactory.getLogger(ds18b20Binding.class);	
		
	/**
	 * The BundleContext. This is only valid when the bundle is ACTIVE. It is set in the activate()
	 * method and must not be accessed anymore once the deactivate() method was called or before activate()
	 * was called.
	 */
	private BundleContext bundleContext;
//	private TreeMap<String, Integer> TemperatureMap = new TreeMap<>();
	
	/** 
	 * the refresh interval which is used to poll values from the ds18b20
	 * server (optional, defaults to 5000ms)
	 */
	private long refreshInterval = 5000;
	
	public ds18b20Binding() {
		logger.debug("ds18b20Binding binding started");
	}
			
	/**
	 * Called by the SCR to activate the component with its configuration read from CAS
	 * 
	 * @param bundleContext BundleContext of the Bundle that defines this component
	 * @param configuration Configuration properties for this component obtained from the ConfigAdmin service
	 */
	public void activate(final BundleContext bundleContext, final Map<String, Object> configuration) {
		this.bundleContext = bundleContext;			
		// to override the default refresh interval one has to add a 
		// parameter to openhab.cfg like <bindingName>:refresh=<intervalInMs>
		String refreshIntervalString = (String) configuration.get("refresh");
		if (StringUtils.isNotBlank(refreshIntervalString)) {
			refreshInterval = Long.parseLong(refreshIntervalString);
		}
		
		setProperlyConfigured(true);
	}
	
	/**
	 * Called by the SCR when the configuration of a binding has been changed through the ConfigAdmin service.
	 * @param configuration Updated configuration properties
	 */
	public void modified(final Map<String, Object> configuration) {
		// update the internal configuration accordingly
	}
	
	/**
	 * Called by the SCR to deactivate the component when either the configuration is removed or
	 * mandatory references are no longer satisfied or the component has simply been stopped.
	 * @param reason Reason code for the deactivation:<br>
	 * <ul>
	 * <li> 0 – Unspecified
     * <li> 1 – The component was disabled
     * <li> 2 – A reference became unsatisfied
     * <li> 3 – A configuration was changed
     * <li> 4 – A configuration was deleted
     * <li> 5 – The component was disposed
     * <li> 6 – The bundle was stopped
     * </ul>
	 */
	public void deactivate(final int reason) {
		this.bundleContext = null;
		// deallocate resources here that are no longer needed and 
		// should be reset when activating this binding again		
	}

	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected String getName() {
		return "ds18b20 Refresh Service";
	}
	

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void execute() {
		// the frequently executed code (polling) goes here ...		
//		logger.debug("ds18b20: ================================================= ");
		readAllTemperatures();
	}
		
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// the code being executed when a command was sent on the openHAB
		// event bus goes here. This method is only called if one of the
		// BindingProviders provide a binding for the given 'itemName'.
//		logger.debug("ds18b20: internalReceiveCommand({},{}) is called!", itemName, command);
		
		//There is no output, just reading. Reading happens automatically through execute... 
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveUpdate({},{}) is called!", itemName, newState);		
	}		
	
	public void readAllTemperatures() {
		for (ds18b20BindingProvider provider : providers) {
			for (String itemName : provider.getItemNames()) {
				try {
					Float tempRaw = (float) readTemperature(provider.getTemperature(itemName).getDeviceId()).getTemperature();
					Float temperature = tempRaw/1000;
					eventPublisher.postUpdate(itemName, DecimalType.valueOf(temperature.toString()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}		
	}
	
	public Temperature readTemperature(String deviceId) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("/sys/bus/w1/devices/" + deviceId + "/w1_slave")));
        if (content != null) {
            Scanner sc = new Scanner(content);
            for (int i = 0; i <= 20; i++) {
                sc.next();
            }
            String tempWithTEquals = sc.next();
            sc.close();
            String temp = tempWithTEquals.substring(2, tempWithTEquals.length());
            Temperature temperature = new Temperature(deviceId);
            temperature.setTemperature(Integer.parseInt(temp));
            temperature.setTimeStamp(System.currentTimeMillis());
            return temperature;
        }
		return null;
    }	
}
