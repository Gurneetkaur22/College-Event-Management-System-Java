package models;

// import java.io.*;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.time.LocalDate;
// import java.time.LocalTime;
import java.util.*;


public class User {
    String username;
    String name;
    List<String[]> registeredEvents = new ArrayList<>(); // Each array: [eventName, clubName]
    List<String> pastEvents = new ArrayList<>();

    public String getName() {
        return username;
    }

    public void addRegisteredEvent(String[] eventDetails) {
        this.registeredEvents.add(eventDetails);
    }

    public List<String[]> getRegisteredEvents() {
        return registeredEvents;
    }

    public List<String> getPastEvents() {
        return pastEvents;
    }

    public User(String username, String name) {
        this.username = username;
        this.name = name;
    }
}
