package models;

// import java.io.*;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


public class Club {
    String clubId;
    String clubName;
    String email;
    String hashedPassword;
    List<Event> hostedEvents = new ArrayList<>();

    public String getClubName() {
        return clubName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public List<Event> getHostedEvents() {
        return hostedEvents;
    }

    public Club(String clubId, String clubName, String email, String hashedPassword) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    public String getClubId() {
        return clubId;
    }

    public String getClubEmail(){
        return email; 
    }


    public void createEvent(String name, String desc, LocalDate date, LocalTime time,String venue, int capacity) {
            Event newEvent = new Event(name, desc, date, time, venue, this.clubName, capacity);
            hostedEvents.add(newEvent);
        }
    }

