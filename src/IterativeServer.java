import java.io.*;
import java.net.*;
import java.util.Scanner;

/*
 * @author Kenny Nguyen, Justin Lambert
 * CNT 4504
 * Summer 2025
 */
public class IterativeServer {
    private static ServerSocket server;
    private static volatile boolean run = true;
    /*
     * main function containing all base function calls and logic
     */
    public static void main(String[] args) {
        System.out.println("please enter port number to initialize server on:");
        Scanner enter = new Scanner(System.in);
        int port = enter.nextInt();

        Thread input = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (run) {
                String exit = scanner.nextLine();
                if (exit.equalsIgnoreCase("quit") || exit.equalsIgnoreCase("q")) {
                    run = false;
                    System.out.println("shutting down server ...");
                    try {
                        if (server != null && !server.isClosed()) {
                            server.close();
                        }
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            } // end first while
        });
        input.start();

        try {
            server = new ServerSocket(port);
            System.out.println("server started on port " + port);
            while (run) {
                Socket client = server.accept();
                System.out.println("client connected: " + client.getInetAddress());
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                String command = in.readLine();
                System.out.println("received command: " + command);

                String response = commandProcess(command);
                out.print("\n" + response);
                out.flush();

                client.close();
            }
            System.out.println("server shutting down. goodbye!");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String commandProcess(String command) {
        String os = System.getProperty("os.name").toLowerCase();
        String result = null;

        try {
            Process process = null;
            switch (command.toLowerCase()) {
                case "date and time":
                    result = new java.util.Date().toString();
                    break;
                case "uptime":
                    if (os.contains("win")) {
                        process = Runtime.getRuntime().exec("net stats srv");
                    } else {
                        process = Runtime.getRuntime().exec("uptime");
                    }
                    break;
                case "memory use":
                    if (os.contains("win")) {
                        process = Runtime.getRuntime().exec("systeminfo");
                    } else if (os.contains("mac")) {
                        process = Runtime.getRuntime().exec("vm_stat");
                    } else {
                        process = Runtime.getRuntime().exec("free -h");
                    }
                    break;
                case "netstat":
                    if (os.contains("win")) {
                        process = Runtime.getRuntime().exec("netstat -an");
                    } else {
                        process = Runtime.getRuntime().exec("netstat -tuln");
                    }
                    break;
                case "current users":
                    if (os.contains("win")) {
                        process = Runtime.getRuntime().exec("query user");
                    } else {
                        process = Runtime.getRuntime().exec("who");
                    }
                    break;
                case "running processes":
                    if (os.contains("win")) {
                        process = Runtime.getRuntime().exec("tasklist");
                    } else {
                        process = Runtime.getRuntime().exec("ps -e");
                    }
                    break;
                default:
                    return "Invalid command";
            }

            // If we ran a system command
            if (process != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                return output.toString();
            }

            return result != null ? result : "No output";

        } catch (IOException e) {
            return "Error executing command: " + e.getMessage();
        }
    }
}
