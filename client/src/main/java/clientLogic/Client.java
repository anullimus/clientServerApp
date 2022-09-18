package clientLogic;

import clientLogic.connection.ConnectionManager;
import clientLogic.logic.Console;
import clientLogic.util.InputManager;
import clientLogic.util.OutputManager;
import myException.DataCantBeSentException;

import java.io.IOException;
import java.nio.channels.UnresolvedAddressException;
import java.util.Collection;
import java.util.HashSet;


public final class Client {
    private static final OutputManager OUTPUT_MANAGER = new OutputManager(System.out);
    private static final Collection<String> LIST_OF_COMMANDS = new HashSet<>();
    private static int serverPort;
    private static int clientPort;
    private static String clientIp;
    private static String serverIp;


    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) {
        initCommandList();
        try {
            initMainInfoForConnection();
        } catch (IOException e) {
            OUTPUT_MANAGER.println("Something went wrong during initialisation client info");
            return;
        }
        try {
            ConnectionManager connectionManager = new ConnectionManager(clientPort, serverPort, clientIp, serverIp, OUTPUT_MANAGER);
            new Console(OUTPUT_MANAGER, new InputManager(System.in), connectionManager, LIST_OF_COMMANDS).start();
        } catch (ClassNotFoundException e) {
            OUTPUT_MANAGER.println("Found incorrect data from server.");
        } catch (IOException e) {
            OUTPUT_MANAGER.println("Something went wrong with IO, the message is: " + e.getMessage());
        } catch (DataCantBeSentException e) {
            OUTPUT_MANAGER.println("Some error happened and we could not sent your registration request to the server. Try again later");
        } catch (UnresolvedAddressException e) {
            OUTPUT_MANAGER.println("You entered incorrect Inet address");
        }
    }

    private static void initCommandList() {
        Client.LIST_OF_COMMANDS.add("add");
        Client.LIST_OF_COMMANDS.add("add_if_min");
        Client.LIST_OF_COMMANDS.add("clear");
        Client.LIST_OF_COMMANDS.add("exit");
        Client.LIST_OF_COMMANDS.add("help");
        Client.LIST_OF_COMMANDS.add("history");
        Client.LIST_OF_COMMANDS.add("info");
        Client.LIST_OF_COMMANDS.add("min_by_id");
        Client.LIST_OF_COMMANDS.add("print_ascending");
        Client.LIST_OF_COMMANDS.add("remove_by_id");
        Client.LIST_OF_COMMANDS.add("remove_greater");
        Client.LIST_OF_COMMANDS.add("show");
        Client.LIST_OF_COMMANDS.add("update");
        Client.LIST_OF_COMMANDS.add("execute_script");
        Client.LIST_OF_COMMANDS.add("filter_less_than_semester_enum");
        Client.LIST_OF_COMMANDS.add("print_ascending");
        Client.LIST_OF_COMMANDS.add("register");
    }


    private static void initMainInfoForConnection() throws IOException {
        serverPort = 7878;
        serverIp = "localhost";
        clientPort = 9999;
        clientIp = "localhost";
    }

}
