import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

class User {
    String username;
    String name;
    List<String[]> registeredEvents = new ArrayList<>(); // Each array: [eventName, clubName]
    List<String> pastEvents = new ArrayList<>();

    User(String username, String name) {
        this.username = username;
        this.name = name;
    }
}

class Club {
    String clubId;
    String clubName;
    String email;
    String hashedPassword;
    List<Event> hostedEvents = new ArrayList<>();

    public String getClubName() {
        return clubName;
    }

    Club(String clubId, String clubName, String email, String hashedPassword) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    void createEvent(String name, String desc, LocalDate date, LocalTime time,String venue, int capacity) {
            Event newEvent = new Event(name, desc, date, time, venue, this.clubName, capacity);
            hostedEvents.add(newEvent);
        }
}

class Event {
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String venue;
    private String club;
    private int capacity;
    Event next;

    public Event(String name, String description, LocalDate date, LocalTime time,
                 String venue, String club, int capacity) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.club = club;
        this.capacity = capacity;
        this.next = null;
    }

    public boolean isUpcoming() {
        return LocalDate.now().isBefore(date);
    }

    
    public String getName() { return name; }
    public String getClub() { return club; }
    public LocalDate getDate() { return date; }
    public String getDetails() {
        return name + " on " + date + " at " + time + ", Venue: " + venue + ", by " + club;
    }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setTime(LocalTime time) { this.time = time; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}

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
                System.out.println("❌ Invalid input. Please enter a number.");
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
        System.out.println("✅ Club registered successfully.");
    }

    static void loginClub() {
        System.out.print("Enter club ID: ");
        String clubId = sc.nextLine();

        if (!clubs.containsKey(clubId)) {
            System.out.println("❌ Club not found!");
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();
        String hashedPassword = hashPassword(password);

        if (clubs.get(clubId).hashedPassword.equals(hashedPassword)) {
            System.out.println("✅ Club login successful!");
            clubDashboard(clubs.get(clubId));
        } else {
            System.out.println("❌ Incorrect password!");
        }
    }

    static void clubDashboard(Club club) {
        while (true) {
            System.out.println("\nClub Dashboard - " + club.clubName);
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
                    saveEventToFile(club.hostedEvents.get(club.hostedEvents.size() - 1));
                    System.out.println("✅ Event created successfully.");

                }
                case 2 -> {
                    System.out.println("📅 Hosted Events:");
                    LocalDate today = LocalDate.now();
                
                    // Sort events by date
                    List<Event> sortedEvents = new ArrayList<>(club.hostedEvents);
                    sortedEvents.sort(Comparator.comparing(Event::getDate));
                
                    for (Event e : sortedEvents) {
                        String status = e.isUpcoming() ? "⏳ Upcoming" : "✅ Completed";
                        System.out.println("- [" + e.getName() + " on " + e.getDate() + "] [" + status + "]");
                    }
                }
                
                case 3 -> {
                    System.out.print("Enter event name to update: ");
                    String eventName = sc.nextLine().trim();
                    Event eventToUpdate = null;
                    for (Event e : club.hostedEvents) {
                        if (e.getName().equalsIgnoreCase(eventName)) {
                            eventToUpdate = e;
                            break;
                        }
                    }
                    if (eventToUpdate == null) {
                        System.out.println("❌ Event not found.");
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
                    System.out.println("✅ Event updated successfully.");
                }
                
                case 4 -> {
                    System.out.print("Enter event name to delete: ");
                    String eventName = sc.nextLine().trim();
                    Event eventToDelete = null;
                    for (Event e : club.hostedEvents) {
                        if (e.getName().equalsIgnoreCase(eventName)) {
                            eventToDelete = e;
                            break;
                        }
                    }
                    if (eventToDelete != null) {
                        club.hostedEvents.remove(eventToDelete);
                        saveAllClubsToFile();
                        System.out.println("✅ Event deleted successfully.");
                    } else {
                        System.out.println("❌ Event not found.");
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
            System.out.println("❌ Club not found.");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();
        String newHashed = hashPassword(newPassword);
        clubs.get(clubId).hashedPassword = newHashed;
        saveAllClubsToFile();
        System.out.println("✅ Club password updated successfully!");
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
        System.out.println("✅ User registered successfully.");
        saveUserToFile(users.get(username), hashedPassword);

    }

    static void loginUser() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        if (!users.containsKey(username)) {
            System.out.println("❌ User not found!");
            return;
        }
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String hashedPasswordInput = hashPassword(password);
        String storedHashedPassword = userCredentials.get(username);

        if (storedHashedPassword.equals(hashedPasswordInput)) {
            System.out.println("✅ User login successful!");
            userDashboard(users.get(username));
        } else {
            System.out.println("❌ Incorrect password!");
        }
    }

    static void userDashboard(User user) {
        while (true) {
            System.out.println("\nUser Dashboard - " + user.name);
            String today = java.time.LocalDate.now().toString();
    
            // Displaying Upcoming Events that the user has not registered for
            System.out.println("📅 Upcoming Events (Not Registered For):");
            List<Event> eventsToDisplay = new ArrayList<>();
            Map<String, String> eventMap = new HashMap<>();
            int count = 1;
    
            // Gathering upcoming events
            for (Club club : clubs.values()) {
                for (Event event : club.hostedEvents) {
                    if (event.isUpcoming() && !isUserAlreadyRegistered(user, event)) {
                        String eventDisplay = count + ". " + event.getName() + " by " + club.clubName + " on " + event.getDate();
                        eventsToDisplay.add(event);
                        eventMap.put(event.getName(), eventDisplay);
                        System.out.println(eventDisplay);
                        count++;
                    }
                }
            }
    
            if (eventsToDisplay.isEmpty()) {
                System.out.println("- No upcoming events available for registration.");
            }
    
            // Displaying Registered Events
            System.out.println("\n📝 Your Registered Events:");
            if (user.registeredEvents.isEmpty()) {
                System.out.println("- You haven't registered for any events yet.");
            } else {
                for (String[] reg : user.registeredEvents) {
                    System.out.println("- " + reg[0] + " by " + reg[1]);
                }                
            }

            System.out.println("Would you like to: ");
            System.out.println("1. Register for an event");
            System.out.println("2. Unregister from an event");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");
            int choice = Integer.parseInt(sc.nextLine());
            // Option for registering or going back
            if (choice == 1) {
                System.out.print("\nEnter event number to register for (or type 'back' to return): ");
                String input = sc.nextLine();
    
                try {
                    int eventIndex = Integer.parseInt(input) - 1;  // Convert to zero-based index
                    if (eventIndex >= 0 && eventIndex < eventsToDisplay.size()) {
                        Event selectedEvent = eventsToDisplay.get(eventIndex);
                        user.registeredEvents.add(new String[]{selectedEvent.getName(), getClubNameByEvent(selectedEvent)});
                        saveAllUsersToFile();

                        System.out.println("✅ Successfully registered for: " + eventMap.get(selectedEvent.getName()));
                    } else {
                        System.out.println("❌ Invalid event number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Please enter a valid event number.");
                }
            } 

            else if(choice == 2) {
                System.out.print("\nEnter event name to unregister from: ");
                String input = sc.nextLine();
    
                if (input.equals("back")) {
                    continue; // Exits the loop if user types 'back'
                }
    
                unregisterUserFromEvent(user, input);
            } 

            else if (choice == 3) {
                return; // Exits the user dashboard
            }
            
            // else {
            //     System.out.print("\nNo upcoming events available. Type 'back' to return: ");
            //     String input = sc.nextLine();
            //     if (input.equals("back")) {
            //         return; // Exits the loop if no events are available
            //     }
            // }
            else {
                System.out.println("❌ Invalid choice!");
            }
        }
    }

    static void unregisterUserFromEvent(User user, String eventName) {
        // Search for the event by name
        Event eventToUnregister = null;
        for (Club club : clubs.values()) {  // Loop through all clubs to find the event
            for (Event e : club.hostedEvents) {
                if (e.getName().equals(eventName)) {
                    eventToUnregister = e;
                    break;
                }
            }
            if (eventToUnregister != null) {
                break;
            }
        }
    
        if (eventToUnregister == null) {
            System.out.println("❌ Event not found.");
            return;
        }
    
        // Check if the user is registered for the event
        boolean isRegistered = false;
        for (String[] reg : user.registeredEvents) {
            if (reg[0].equals(eventName)) {
                isRegistered = true;
                break;
            }
        }
    
        if (!isRegistered) {
            System.out.println("❌ You are not registered for this event.");
            return;
        }
    
        // Remove the event registration
        List<String[]> updatedRegistrations = new ArrayList<>();
        for (String[] reg : user.registeredEvents) {
            if (!reg[0].equals(eventName)) {
                updatedRegistrations.add(reg);
            }
        }
    
        // Update the user's registration list
        user.registeredEvents = updatedRegistrations;
        System.out.println("✅ Successfully unregistered from event: " + eventName);
        
        // Optionally, save the updated user data to the file
        saveUserData(user);
    }
    
    static void saveUserData(User user) {
        // Assuming you want to save the updated user information to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", false))) {
            for (User u : users.values()) {
                StringBuilder sb = new StringBuilder();
                sb.append(u.username).append(",").append(u.name).append(",").append(userCredentials.get(u.username));
                for (String[] regEvent : u.registeredEvents) {
                    sb.append(",").append(regEvent[0]).append(":").append(regEvent[1]);
                }
                for (String pastEvent : u.pastEvents) {
                    sb.append(",").append(pastEvent);
                }
                writer.write(sb.toString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving user data.");
        }
    }
    
    
    
    

    static void forgotPasswordUser() {
        System.out.print("Enter your username: ");
        String username = sc.nextLine();

        if (!userCredentials.containsKey(username)) {
            System.out.println("❌ User not found.");
            return;
        }
        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();
        String newHashed = hashPassword(newPassword);
        userCredentials.put(username, newHashed);
        System.out.println("✅ Password updated successfully!");
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            StringBuilder sb = new StringBuilder();
            sb.append(user.username).append(",").append(user.name).append(",").append(hashedPassword);

            for (String[] regEvent : user.registeredEvents) {
                sb.append(",").append(regEvent[0]).append(":").append(regEvent[1]);
            }
            for (String pastEvent : user.pastEvents) {
                sb.append(",").append(pastEvent);
            }
            writer.write(sb.toString() + "\n");

        } catch (IOException e) {
            System.out.println("Error writing user to file.");
        }
    }

    static void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
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
                            user.registeredEvents.add(new String[]{eventClub[0], eventClub[1]});
                        }
                    }
                }
                
            }
        } catch (IOException ignored) {}
    }
    
    
    
    static void saveClubToFile(Club club) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("clubs.txt", true))) {
            writer.write(club.clubId + "," + club.clubName + "," + club.email + "," + club.hashedPassword + "\n");
        } catch (IOException e) {
            System.out.println("Error writing club to file.");
        }
    }
    
    static void loadClubsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("clubs.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                clubs.put(parts[0], new Club(parts[0], parts[1], parts[2], parts[3]));
            }
        } catch (IOException ignored) {}
    }
    
    static void saveAllClubsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("clubs.txt"))) {
            for (Club club : clubs.values()) {
                writer.write(club.clubId + "," + club.clubName + "," + club.email + "," + club.hashedPassword + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving clubs.");
        }
    }

    static void saveEventToFile(Event event) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("events.txt", true))) {
            writer.write( event.getName() + "," + event.getClub() + "," +
                         event.getDate() + "," + event.getDetails() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving event to file.");
        }
    }
    

    static void loadEventsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("events.txt"))) {
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
                        clubs.get(clubName).hostedEvents.add(event);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading events from file.");
        }
    }

    static String getClubNameByEvent(Event targetEvent) {
        for (Club club : clubs.values()) {
            for (Event e : club.hostedEvents) {
                if (e.getName().equalsIgnoreCase(targetEvent.getName())) {
                    return club.getClubName();
                }
            }
        }
        return "UnknownClub";
    }
    
    

    static boolean isUserAlreadyRegistered(User user, Event event) {
        for (String[] reg : user.registeredEvents) {
            if (reg[1].equalsIgnoreCase(event.getName())) {
                return true;
            }
        }
        return false;
    }
    

    

    static void saveAllUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt"))) {
            for (Map.Entry<String, User> entry : users.entrySet()) {
                User user = entry.getValue();
                String hashed = userCredentials.get(user.username);
                StringBuilder sb = new StringBuilder();
                sb.append(user.username).append(",").append(user.name).append(",").append(hashed);
                for (String[] regEvent : user.registeredEvents) {
                    sb.append(",").append(regEvent[0]).append(":").append(regEvent[1]);
                }
                writer.write(sb.toString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving all users.");
        }
    }
    
    
    
    
}