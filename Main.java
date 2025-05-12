import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import models.Club;
import models.Event;
import models.User;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static HashMap<String, User> users = new HashMap<>();
    static HashMap<String, Club> clubs = new HashMap<>();
    static HashMap<String, String> userCredentials = new HashMap<>();

    public static void main(String[] args) {
        loadClubsFromFile();
        loadUsersFromFile();
        loadEventsFromFile(); 

        while (true) {
            System.out.println("\nWelcome to College Event Management System");
            System.out.println("Who are you?");
            System.out.println("1. Club (Organizer)");
            System.out.println("2. User (Student/Participant)");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(" Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> clubFlow();
                case 2 -> userFlow();
                case 3 -> {
                    System.out.println("Thank you for using the system!");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // Club Flow (Register/Login as Club)
    static void clubFlow() {
        while (true) {
            System.out.println("\nClub Menu:");
            System.out.println("1. Register as Club");
            System.out.println("2. Login as Club");
            System.out.println("3. Forgot Password");
            System.out.println("4. Back");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1 -> registerClub();
                case 2 -> loginClub();
                case 3 -> forgotPasswordClub();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void registerClub() {
        System.out.print("Enter club ID: ");
        String clubId = sc.nextLine();
        if (clubs.containsKey(clubId)) {
            System.out.println("Club already exists!");
            return;
        }
        System.out.print("Enter club name: ");
        String name = sc.nextLine();
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Create password: ");
        String password = sc.nextLine();

        String hashedPassword = hashPassword(password);
        Club club = new Club(clubId, name, email, hashedPassword);
        clubs.put(clubId, club);
        saveClubToFile(club);
        System.out.println(" Club registered successfully.");
    }

    static void loginClub() {
        System.out.print("Enter club ID: ");
        String clubId = sc.nextLine();

        if (!clubs.containsKey(clubId)) {
            System.out.println(" Club not found!");
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();
        String hashedPassword = hashPassword(password);

        if (clubs.get(clubId).getHashedPassword().equals(hashedPassword)) {
            System.out.println(" Club login successful!");
            clubDashboard(clubs.get(clubId));
        } else {
            System.out.println(" Incorrect password!");
        }
    }

    static void clubDashboard(Club club) {
        while (true) {
            System.out.println("\nClub Dashboard - " + club.getClubName());
            System.out.println("1. Create Event");
            System.out.println("2. View Events");
            System.out.println("3. Update Event");
            System.out.println("4. Delete Event");
            System.out.println("5. Logout");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1 -> {
                    System.out.print("Event name: ");
                    String name = sc.nextLine();
                    System.out.print("Description: ");
                    String desc = sc.nextLine();
                    System.out.print("Date (YYYY-MM-DD): ");
                    LocalDate date = LocalDate.parse(sc.nextLine());
                    System.out.print("Time (HH:MM): ");
                    LocalTime time = LocalTime.parse(sc.nextLine());
                    System.out.print("Venue: ");
                    String venue = sc.nextLine();
                    System.out.print("Capacity: ");
                    int capacity = Integer.parseInt(sc.nextLine());

                    club.createEvent(name, desc, date, time, venue, capacity);
                    saveEventToFile(club.getHostedEvents().get(club.getHostedEvents().size() - 1));
                    System.out.println(" Event created successfully.");

                }
                case 2 -> {
                    System.out.println(" Hosted Events:");
                    // LocalDate today = LocalDate.now();
                
                    // Sort events by date
                    List<Event> sortedEvents = new ArrayList<>(club.getHostedEvents());
                    sortedEvents.sort(Comparator.comparing(Event::getDate));
                
                    for (Event e : sortedEvents) {
                        String status = e.isUpcoming() ? " Upcoming" : " Completed";
                        System.out.println("- [" + e.getName() + " on " + e.getDate() + "] [" + status + "]");
                    }
                }
                
                case 3 -> {
                    System.out.print("Enter event name to update: ");
                    String eventName = sc.nextLine().trim();
                    Event eventToUpdate = null;
                    for (Event e : club.getHostedEvents()) {
                        if (e.getName().equalsIgnoreCase(eventName)) {
                            eventToUpdate = e;
                            break;
                        }
                    }
                    if (eventToUpdate == null) {
                        System.out.println(" Event not found.");
                        continue;
                    }
                
                    System.out.print("New name: ");
                    String name = sc.nextLine();
                    System.out.print("New description: ");
                    String desc = sc.nextLine();
                    System.out.print("New date (YYYY-MM-DD): ");
                    LocalDate date = LocalDate.parse(sc.nextLine());
                    System.out.print("New time (HH:MM): ");
                    LocalTime time = LocalTime.parse(sc.nextLine());
                    System.out.print("New venue: ");
                    String venue = sc.nextLine();
                    System.out.print("New capacity: ");
                    int capacity = Integer.parseInt(sc.nextLine());
                
                    eventToUpdate.setName(name);
                    eventToUpdate.setDescription(desc);
                    eventToUpdate.setDate(date);
                    eventToUpdate.setTime(time);
                    eventToUpdate.setVenue(venue);
                    eventToUpdate.setCapacity(capacity);
                    saveAllClubsToFile();
                    System.out.println(" Event updated successfully.");
                }
                
                case 4 -> {
                    System.out.print("Enter event name to delete: ");
                    String eventName = sc.nextLine().trim();
                    Event eventToDelete = null;
                    for (Event e : club.getHostedEvents()) {
                        if (e.getName().equalsIgnoreCase(eventName)) {
                            eventToDelete = e;
                            break;
                        }
                    }
                    if (eventToDelete != null) {
                        club.getHostedEvents().remove(eventToDelete);
                        saveAllClubsToFile();
                        System.out.println(" Event deleted successfully.");
                    } else {
                        System.out.println(" Event not found.");
                    }
                }
                

                case 5 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    

    static void forgotPasswordClub() {
        System.out.print("Enter your club ID: ");
        String clubId = sc.nextLine();

        if (!clubs.containsKey(clubId)) {
            System.out.println(" Club not found.");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();
        String newHashed = hashPassword(newPassword);
        clubs.get(clubId).setHashedPassword(newHashed);
        saveAllClubsToFile();
        System.out.println(" Club password updated successfully!");
    }

    // User Flow (Register/Login as User)
    static void userFlow() {
        while (true) {
            System.out.println("\nUser Menu:");
            System.out.println("1. Register as User");
            System.out.println("2. Login as User");
            System.out.println("3. Forgot Password");
            System.out.println("4. Back");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1 -> registerUser();
                case 2 -> loginUser();
                case 3 -> forgotPasswordUser();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void registerUser() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        if (users.containsKey(username)) {
            System.out.println("User already exists!");
            return;
        }
        System.out.print("Enter name: ");
        String name = sc.nextLine();
        System.out.print("Create password: ");
        String password = sc.nextLine();

        String hashedPassword = hashPassword(password);
        users.put(username, new User(username, name));
        userCredentials.put(username, hashedPassword);
        System.out.println(" User registered successfully.");
        saveUserToFile(users.get(username), hashedPassword);

    }

    static void loginUser() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        if (!users.containsKey(username)) {
            System.out.println(" User not found!");
            return;
        }
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String hashedPasswordInput = hashPassword(password);
        String storedHashedPassword = userCredentials.get(username);

        if (storedHashedPassword.equals(hashedPasswordInput)) {
            System.out.println(" User login successful!");
            userDashboard(users.get(username));
        } else {
            System.out.println(" Incorrect password!");
        }
    }

    static void userDashboard(User user) {
        while (true) {
            System.out.println("\nUser Dashboard - " + user.getName());
            System.out.println("1. View & Register for Upcoming Events");
            System.out.println("2. View Your Registered Events");
            System.out.println("3. Cancel Event Registration");
            System.out.println("4. View Past Events");
            System.out.println("5. Logout");
            System.out.print("Choose option: ");
        
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1 -> {
                    List<Event> upcomingEvents = new ArrayList<>();
                    for (Club club : clubs.values()) {
                        for (Event event : club.getHostedEvents()) {
                            if (event.isUpcoming() && !isUserAlreadyRegistered(user, event)) {
                                upcomingEvents.add(event);
                            }
                        }
                    }
        
                    if (upcomingEvents.isEmpty()) {
                        System.out.println(" No new upcoming events available.");
                        break;
                    }
        
                    System.out.println("\n Upcoming Events:");
                    for (int i = 0; i < upcomingEvents.size(); i++) {
                        Event e = upcomingEvents.get(i);
                        System.out.println((i + 1) + ". " + e.getDetails() );
                    }
        
                    System.out.print("Enter event number to register: ");
                    int eventChoice = Integer.parseInt(sc.nextLine()) - 1;
                    if (eventChoice >= 0 && eventChoice < upcomingEvents.size()) {
                        Event selectedEvent = upcomingEvents.get(eventChoice);
                        
                        // Check if the event is at full capacity
                        if (selectedEvent.getRegisteredUsers().size() < selectedEvent.getCapacity()) {
                            // Register the user for the event
                            selectedEvent.getRegisteredUsers().add(user.getName());
                            user.addRegisteredEvent(new String[]{selectedEvent.getName(), selectedEvent.getClub()});
                            System.out.println(" Successfully registered for the event: " + selectedEvent.getName());
                            // saveEventToFile(selectedEvent);
                        } else {
                            // Add the user to the waitlist
                            selectedEvent.getWaitlist().offer(user.getName());
                            System.out.println(" Event is at full capacity! You have been added to the waitlist.");
                            // saveEventToFile(selectedEvent);
                        }
                    } else {
                        System.out.println(" Invalid event number.");
                    }
                }
                case 2 -> {
                    System.out.println("\n Your Registered Events:");
                    if (user.getRegisteredEvents().isEmpty()) {
                        System.out.println("You have not registered for any events.");
                    } else {
                        for (String[] eventDetails : user.getRegisteredEvents()) {
                            System.out.println("- " + eventDetails[0] + " hosted by " + eventDetails[1]);
                        }
                    }
                }
                case 3 -> {
                    System.out.print("Enter event name to cancel registration: ");
                    String eventName = sc.nextLine().trim();
                    Event eventToCancel = null;
                    for (String[] eventDetails : user.getRegisteredEvents()) {
                        if (eventDetails[0].equalsIgnoreCase(eventName)) {
                            eventToCancel = getEventByName(eventDetails[0], eventDetails[1]);
                            break;
                        }
                    }
                    if (eventToCancel != null) {
                        eventToCancel.getRegisteredUsers().remove(user.getName());
                        user.getRegisteredEvents().removeIf(e -> e[0].equals(eventName));
                        System.out.println(" Registration canceled for event: " + eventName);
                        // Check if anyone on the waitlist can be registered now
                        if (!eventToCancel.getWaitlist().isEmpty()) {
                            String waitlistedUser = eventToCancel.getWaitlist().poll();
                            eventToCancel.getRegisteredUsers().add(waitlistedUser);
                            System.out.println(" Waitlisted user " + waitlistedUser + " has been moved to registered list.");
                        }
                        saveEventToFile(eventToCancel);
                    } else {
                        System.out.println(" You are not registered for the event: " + eventName);
                    }
                }
                case 4 -> {
                    System.out.println("\n Your Past Events:");
                    if (user.getPastEvents().isEmpty()) {
                        System.out.println("You have not attended any events.");
                    } else {
                        for (String eventName : user.getPastEvents()) {
                            System.out.println("- " + eventName);
                        }
                    }
                }
                case 5 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    
    

    static void forgotPasswordUser() {
        System.out.print("Enter your username: ");
        String username = sc.nextLine();

        if (!userCredentials.containsKey(username)) {
            System.out.println(" User not found.");
            return;
        }
        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();
        String newHashed = hashPassword(newPassword);
        userCredentials.put(username, newHashed);
        System.out.println(" Password updated successfully!");
    }
    
    static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash)
                hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    static void saveUserToFile(User user, String hashedPassword) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/users.txt", true))) {
            StringBuilder sb = new StringBuilder();
            sb.append(user.getName()).append(",").append(user.getName()).append(",").append(hashedPassword);

            for (String[] regEvent : user.getRegisteredEvents()) {
                sb.append(",").append(regEvent[0]).append(":").append(regEvent[1]);
            }
            for (String pastEvent : user.getPastEvents()) {
                sb.append(",").append(pastEvent);
            }
            writer.write(sb.toString() + "\n");

        } catch (IOException e) {
            System.out.println("Error writing user to file.");
        }
    }

    static void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    User user = new User(parts[0], parts[1]);
                    users.put(parts[0], user);
                    userCredentials.put(parts[0], parts[2]);
                
                    // Load registered events
                    for (int i = 3; i < parts.length; i++) {
                        String[] eventClub = parts[i].split(":");
                        if (eventClub.length == 2) {
                            user.getRegisteredEvents().add(new String[]{eventClub[0], eventClub[1]});
                        }
                    }
                }
                
            }
        } catch (IOException ignored) {}
    }
    
    
    
    static void saveClubToFile(Club club) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/clubs.txt", true))) {
            writer.write(club.getClubId() + "," + club.getClubName() + "," + club.getClubEmail() + "," + club.getHashedPassword() + "\n");
        } catch (IOException e) {
            System.out.println("Error writing club to file.");
        }
    }
    
    static void loadClubsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/clubs.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                clubs.put(parts[0], new Club(parts[0], parts[1], parts[2], parts[3]));
            }
        } catch (IOException ignored) {}
    }
    
    static void saveAllClubsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/clubs.txt"))) {
            for (Club club : clubs.values()) {
                writer.write(club.getClubId() + "," + club.getClubName() + "," + club.getClubEmail() + "," + club.getHashedPassword() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving clubs.");
        }
    }

    static void saveEventToFile(Event event) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/events.txt", true))) {
            writer.write( event.getName() + "," + event.getClub() + "," +
                         event.getDate() + "," + event.getDetails() +  "," + event.getCapacity() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving event to file.");
        }
    }
    

    static void loadEventsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/events.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String eventName = parts[0];
                    String eventDate = parts[2];
                    String clubName = parts[1];
                    LocalDate date = LocalDate.parse(eventDate);
                    Event event = new Event(eventName, "", date, LocalTime.MIDNIGHT, "", clubName, 0);
    
                    // Add the event to the respective club
                    if (clubs.containsKey(clubName)) {
                        clubs.get(clubName).getHostedEvents().add(event);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading events from file.");
        }
    }

    static String getClubNameByEvent(Event targetEvent) {
        for (Club club : clubs.values()) {
            for (Event e : club.getHostedEvents()) {
                if (e.getName().equalsIgnoreCase(targetEvent.getName())) {
                    return club.getClubName();
                }
            }
        }
        return "UnknownClub";
    }
    
    

    static boolean isUserAlreadyRegistered(User user, Event event) {
        for (String[] reg : user.getRegisteredEvents()) {
            if (reg[0].equalsIgnoreCase(event.getName()) && reg[1].equalsIgnoreCase(event.getClub())) {
                return true;
            }
        }
        return false;
    }
    

    

    static void saveAllUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/users.txt"))) {
            for (Map.Entry<String, User> entry : users.entrySet()) {
                User user = entry.getValue();
                String hashed = userCredentials.get(user.getName());
                StringBuilder sb = new StringBuilder();
                sb.append(user.getName()).append(",").append(",").append(hashed);
                for (String[] regEvent : user.getRegisteredEvents()) {
                    sb.append(",").append(regEvent[0]).append(":").append(regEvent[1]);
                }
                writer.write(sb.toString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving all users.");
        }
    }

    // Helper method to get event by name and club
    static Event getEventByName(String eventName, String clubName) {
        for (Club club : clubs.values()) {
            for (Event event : club.getHostedEvents()) {
                if (event.getName().equalsIgnoreCase(eventName) && event.getClub().equalsIgnoreCase(clubName)) {
                    return event;
                }
            }
        }
        return null;
    }
    
    static Event getEventByNameAndClub(String name, String clubName) {
        Club club = null;
        for (Club c : clubs.values()) {
            if (c.getClubName().equals(clubName)) {
                club = c;
                break;
            }
        }
        if (club != null) {
            for (Event e : club.getHostedEvents()) {
                if (e.getName().equals(name)) {
                    return e;
                }
            }
        }
        return null;
    }
}
