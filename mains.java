import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

class User {
    String username;
    String name;
    List<String> registeredEvents = new ArrayList<>();

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
    List<String> hostedEvents = new ArrayList<>();

    Club(String clubId, String clubName, String email, String hashedPassword) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }
}

public class Main {
    static Scanner sc = new Scanner(System.in);
    static HashMap<String, User> users = new HashMap<>();
    static HashMap<String, Club> clubs = new HashMap<>();
    static HashMap<String, String> userCredentials = new HashMap<>();

    public static void main(String[] args) {
        loadClubsFromFile();
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
            System.out.println("2. Delete Event");
            System.out.println("3. Update Event");
            System.out.println("4. View My Events");
            System.out.println("5. View Registrations");
            System.out.println("6. Logout");
            System.out.println("7. Back");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter event name: ");
                    String event = sc.nextLine();
                    club.hostedEvents.add(event);
                    System.out.println("Event added.");
                }
                case 2 -> {
                    System.out.print("Enter event name to delete: ");
                    String event = sc.nextLine();
                    club.hostedEvents.remove(event);
                    System.out.println("Event deleted.");
                }
                case 3 -> {
                    System.out.print("Enter old event name: ");
                    String oldEvent = sc.nextLine();
                    System.out.print("Enter new event name: ");
                    String newEvent = sc.nextLine();
                    if (club.hostedEvents.contains(oldEvent)) {
                        club.hostedEvents.remove(oldEvent);
                        club.hostedEvents.add(newEvent);
                        System.out.println("Event updated.");
                    } else {
                        System.out.println("Event not found.");
                    }
                }
                case 4 -> {
                    System.out.println("Hosted Events:");
                    for (String e : club.hostedEvents) {
                        System.out.println("- " + e);
                    }
                }
                case 5 -> System.out.println("Feature under development.");
                case 6, 7 -> {
                    return;
                }
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
        } else {
            System.out.println("❌ Incorrect password!");
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
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: SHA-256 not found");
        }
    }

    static void saveClubToFile(Club club) {
        try (FileWriter fw = new FileWriter("clubs.txt", true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(club.clubId + "," + club.clubName + "," + club.email + "," + club.hashedPassword);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file.");
        }
    }

    static void saveAllClubsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("clubs.txt"))) {
            for (Club club : clubs.values()) {
                bw.write(club.clubId + "," + club.clubName + "," + club.email + "," + club.hashedPassword);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating file.");
        }
    }

    static void loadClubsFromFile() {
        File file = new File("clubs.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String id = parts[0];
                    String name = parts[1];
                    String email = parts[2];
                    String password = parts[3];
                    clubs.put(id, new Club(id, name, email, password));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading club data.");
        }
    }
}
