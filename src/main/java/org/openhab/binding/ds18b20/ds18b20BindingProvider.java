/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ds18b20;

import java.util.TreeMap;

import org.openhab.binding.ds18b20.internal.Temperature;
import org.openhab.core.binding.BindingProvider;
import com.pi4j.io.i2c.I2CDevice;


/**
 * @author MichaelP
 * @since 1.0
 */
public interface ds18b20BindingProvider extends BindingProvider {

	public Temperature getTemperature(String itemName);
	
}
