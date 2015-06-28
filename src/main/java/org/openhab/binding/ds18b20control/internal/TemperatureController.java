package home.control.controller;

import com.google.gson.Gson;
import home.control.Server;
import home.control.model.Temperature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TemperatureController {

    private Gson gson;
    private Temperature temperature;

    public TemperatureController(Temperature temperature) {
        this.gson = new Gson();
        this.temperature = temperature;
    }

    /**
     * Receives an Temperature Object with an deviceId and empty temperature value.
     * Reads temp value from Filesysystem located in: /sys/bus/w1/devices/<deviceId>//w1_slave
     * @return Temperature Object with value read from filesystem
     * @throws IOException
     */
    public void sendTemperature() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("/sys/bus/w1/devices/" + temperature.getDeviceId() + "/w1_slave")));
        if (content != null) {
            Scanner sc = new Scanner(content);
            for (int i = 0; i <= 20; i++) {
                sc.next();
            }
            String tempWithTEquals = sc.next();
            sc.close();
            String temp = tempWithTEquals.substring(2, tempWithTEquals.length());
            System.out.println("Temp: " + temp);
            temperature.setTemperature(Integer.parseInt(temp));
            temperature.setTimeStamp(System.currentTimeMillis());
            Server.socket.send(gson.toJson(temperature));
        }
    }

}
