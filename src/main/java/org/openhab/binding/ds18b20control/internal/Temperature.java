package home.control.model;

public class Temperature {

    private final Event event = Event.TEMP;
    private String deviceId;
    private String name;
    private long timeStamp;
    private int temperature;

    public Temperature(String deviceId) {
        this.deviceId = deviceId;
    }

    public Temperature(String deviceId, String name, long timeStamp, int temperature) {
        this.deviceId = deviceId;
        this.name = name;
        this.timeStamp = timeStamp;
        this.temperature = temperature;
    }

    public Event getEvent() {
        return event;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

}
