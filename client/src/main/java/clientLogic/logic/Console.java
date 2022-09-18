package clientLogic.logic;

import clientLogic.commands.ExecuteScriptCommand;
import clientLogic.connection.ConnectionManager;
import clientLogic.util.DataObjectsMaker;
import clientLogic.util.InputManager;
import clientLogic.util.OutputManager;
import command.*;
import data.initial.StudyGroup;
import myException.DataCantBeSentException;
import util.Request;
import util.Response;

import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.UnresolvedAddressException;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;


public class Console {
    private static final int MAX_STRING_LENGTH = 100;
    private final OutputManager outputManager;
    private final InputManager inputManager;
    private final ConnectionManager connectionManager;
    private final Collection<String> listOfCommands;
    private String username;
    private String password;
    private final java.io.Console terminal = System.console();


    public Console(
            OutputManager outputManager,
            InputManager inputManager,
            ConnectionManager connectionManager,
            Collection<String> listOfCommands
    ) {
        this.outputManager = outputManager;
        this.inputManager = inputManager;
        this.connectionManager = connectionManager;
        this.listOfCommands = listOfCommands;
    }

    public void start() throws ClassNotFoundException, IOException, DataCantBeSentException, UnresolvedAddressException {
        initUsernameAndPassword();
        DataObjectsMaker dataObjectsMaker = new DataObjectsMaker(inputManager, outputManager, username);
        System.out.println("Try the work with the command \"help\".");
        String input;
        do {
            input = readNextCommand();
            if ("exit".equals(input)) {
                break;
            }
            final String[] parsedInp = parseToNameAndArg(input);
            final String commandName = parsedInp[0];
            Serializable commandArg = parsedInp[1];
            String commandArg2 = ""; // only for update command in this case
            if (listOfCommands.contains(commandName)) {
                if ("add".equals(commandName) || "add_if_min".equals(commandName) || "remove_greater".equals(commandName)) {
                    commandArg = dataObjectsMaker.makeStudyGroup();
                }
                if ("update".equals(commandName)) {
                    commandArg2 = (String) commandArg;
                    commandArg = dataObjectsMaker.makeStudyGroup();
                }

                if ("execute_script".equals(commandName)) {
                    new ExecuteScriptCommand((String) commandArg).execute(inputManager);
                } else {
                    try {
                        outputManager.println(connectionManager.sendCommand(new Request(getCommandObjectByName
                                (commandName, commandArg, commandArg2), username, password)).getOutput().toString());
                    } catch (DataCantBeSentException e) {
                        outputManager.println("Could not send a command");
                    }
                }
            } else {
                outputManager.println("The command was not found. Please use \"help\" to know about commands.");
            }
        } while (true);
    }

    private void initUsernameAndPassword() throws IOException, DataCantBeSentException, UnresolvedAddressException {
        outputManager.println("Would you like to register first? (type \"yes\" to register or something else to continue with your own password+login).");
        final String answer = inputManager.nextLine();
        if ("yes".equals(answer)) {
            outputManager.print("Enter your new login: ");
            final String loginToRegister = inputManager.nextLine();
            outputManager.print("Enter new password: ");
            String passwordToRegister;
            try {
                char[] passwd = terminal.readPassword("Enter password: ");
                passwordToRegister = Arrays.toString(passwd);
            }catch (NullPointerException nullPointerException){
                System.out.print("Enter password: ");
                passwordToRegister = inputManager.nextLine();
            }

            if (loginToRegister.length() > MAX_STRING_LENGTH || passwordToRegister.length() > MAX_STRING_LENGTH) {
                outputManager.println("Your password or login was too long");
                return;
            }

            Response registerCommandResult = connectionManager.sendCommand(new Request(new RegisterCommand(new String[]{loginToRegister, passwordToRegister})));
            if (registerCommandResult.isWasExecutedCorrectly()) {
                if (!((RegisterCommand.RegisterCommandResult) registerCommandResult).isWasRegistered()) {
                    outputManager.println("User hasn't been registered because the username was not unique.");
                    initUsernameAndPassword();
                } else {
                    password = passwordToRegister;
                    username = loginToRegister;
                }
            } else {
                throw new DataCantBeSentException();
            }
        } else {
            outputManager.print("Enter login: ");
            username = inputManager.nextLine();
            try {
                char[] passwd = terminal.readPassword("Enter password: ");
                password = Arrays.toString(passwd);
            }catch (NullPointerException nullPointerException){
                System.out.print("Enter password: ");
                password = inputManager.nextLine();
            }
        }
    }

    private String[] parseToNameAndArg(String input) {
        String[] arrayInput = input.split(" ");
        String inputCommand = arrayInput[0];
        String inputArg = "";

        if (arrayInput.length >= 2) {
            inputArg = arrayInput[1];
        }

        return new String[]{inputCommand, inputArg};
    }

    private String readNextCommand() throws IOException {
        outputManager.print(">>>");
        try {
            return inputManager.nextLine();
        } catch (NoSuchElementException e) {
            return "exit";
        }
    }

    private Command getCommandObjectByName(String commandName, Serializable arg, String arg2) {
        Command command;
        switch (commandName) {
            case "add":
                command = new AddCommand((StudyGroup) arg);
                break;
            case "add_if_min":
                command = new AddIfMinCommand((StudyGroup) arg);
                break;
            case "clear":
                command = new ClearCommand();
                break;
            case "filter_less_than_semester_enum":
                command = new FilterLessThanSemesterEnumCommand((String) arg);
                break;
            case "history":
                command = new HistoryCommand();
                break;
            case "info":
                command = new InfoCommand();
                break;
            case "min_by_id":
                command = new MinByIDCommand((String) arg);
                break;
            case "print_ascending":
                command = new PrintAscendingCommand();
                break;
            case "remove_by_id":
                command = new RemoveByIdCommand((String) arg);
                break;
            case "remove_greater":
                command = new RemoveGreaterCommand((StudyGroup) arg);
                break;
            case "show":
                command = new ShowCommand();
                break;
            case "update":
                command = new UpdateCommand((StudyGroup) arg, arg2);
                break;
            default:
                command = new HelpCommand();
                break;
        }
        return command;
    }
}
