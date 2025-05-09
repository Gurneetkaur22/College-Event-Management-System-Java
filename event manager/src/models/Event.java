package models;

// import java.io.*;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


public class Event {
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String venue;
    private String club;
    private int capacity;
    private List<String> registeredUsers = new ArrayList<>();
    private Queue<String> waitlist = new LinkedList<>();

    public Event(String name, String description, LocalDate date, LocalTime time, String venue, String club, int capacity) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.club = club;
        this.capacity = capacity;
    }

    public List<String> getRegisteredUsers() {
        return registeredUsers;
    }

    public boolean isUpcoming() {
        return LocalDate.now().isBefore(date);
    }

    public String getDetails() {
        return name + " on " + date + " at " + time + ", Venue: " + venue + ", by " + club;
    }

    public int getCapacity() {
        return capacity;
    }
    public Queue<String> getWaitlist() {
        return waitlist;
    }

    public String getName() { return name; }
    public String getClub() { return club; }
    public LocalDate getDate() { return date; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setTime(LocalTime time) { this.time = time; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}

