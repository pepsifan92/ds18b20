# ds18b20
Own binding for get temperature of 1wire sensor DS18B20 on Raspberry for openHAB

I didn't get run the available onewire binding or its related sofware owfs...
The chip DS18B20 is an 1-Wire temperature sensor. This binding reads simply the value of temperature from "/sys/bus/w1/devices/" + deviceId + "/w1_slave"

## Requires on Raspberry Pi
Connect one or more 1Wire DS18B20 chip(s) to the Pi<br>
edit `/boot/config.txt`<br>
append: `dtoverlay=w1-gpio,gpiopin=4`<br>
reboot.<br>

**Get the deviceId of every chip:**
`ls /sys/bus/w1/devices`<br>
a deviceId looks like: *28-00044a70e9ff*

## Configexample in *.item file
In the **items-file** of openHAB the following **configuration** is needed:<br>
Number temperature {ds18b20="28-00044a70e9ff"}

## Configexample in *.sitemap file
Text item=temperature label="Temperature: [%.1f]"	
