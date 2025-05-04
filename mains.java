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
    String username;
    String name;
    List<String> hostedEvents = new ArrayList<>();

    Club(String username, String name) {
        this.username = username;
        this.name = name;
    }
}

public class mains {
    static Scanner sc = new Scanner(System.in);
    static HashMap<String, User> users = new HashMap<>();
    static HashMap<String, Club> clubs = new HashMap<>();
    static HashMap<String, String> userCredentials = new HashMap<>();
    static HashMap<String, String> clubCredentials = new HashMap<>();

    public static void main(String[] args) {
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
        System.out.print("Enter club username: ");
        String username = sc.nextLine();
        if (clubs.containsKey(username)) {
            System.out.println("Club already exists!");
            return;
        }
        System.out.print("Enter club name: ");
        String name = sc.nextLine();
        System.out.print("Create password: ");
        String password = sc.nextLine();

        String hashedPassword = hashPassword(password);
        clubs.put(username, new Club(username, name));
        clubCredentials.put(username, hashedPassword);
        System.out.println("✅ Club registered successfully.");
    }

    static void loginClub() {
        System.out.print("Enter club username: ");
        String username = sc.nextLine();

        if (!clubs.containsKey(username)) {
            System.out.println("❌ Club not found!");
            return;
        }
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String hashedPasswordInput = hashPassword(password);
        String storedHashedPassword = clubCredentials.get(username);

        if (storedHashedPassword.equals(hashedPasswordInput)) {
            System.out.println("✅ Club login successful!");
     
        } else {
            System.out.println("❌ Incorrect password!");
        }
    }

    static void forgotPasswordClub() {
        System.out.print("Enter your club username: ");
        String username = sc.nextLine();

        if (!clubCredentials.containsKey(username)) {
            System.out.println("❌ Club not found.");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();
        String newHashed = hashPassword(newPassword);

        clubCredentials.put(username, newHashed);
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
}
