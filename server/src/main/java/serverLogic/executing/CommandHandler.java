package serverLogic.executing;


import command.PrivateAccessedStudyGroupCommand;
import command.RegisterCommand;
import javafx.util.Pair;
import util.*;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class CommandHandler {
    private final Queue<Pair<Request, SocketAddress>> queueToBeExecuted;
    private final Queue<Pair<Response, SocketAddress>> queueToBeSent;
    private final DataManager dataManager;
    private final HistoryManager historyManager;

    public CommandHandler(Queue<Pair<Request, SocketAddress>> queueToBeExecuted, Queue<Pair<Response,
            SocketAddress>> queueToBeSent, DataManager dataManager, HistoryManager historyManager) {
        this.queueToBeExecuted = queueToBeExecuted;
        this.queueToBeSent = queueToBeSent;
        this.dataManager = dataManager;
        this.historyManager = historyManager;
    }

    public void startToHandleCommands(State<Boolean> isWorkingState, ExecutorService cashedThreadPool) {
        Runnable startCheckingForAvailableCommandsToRun = new Runnable() {
            @Override
            public void run() {
                while (isWorkingState.getValue()) {
                    if (!queueToBeExecuted.isEmpty()) {
                        Pair<Request, SocketAddress> pairOfCommandAndClientAddress = queueToBeExecuted.poll();
                        Runnable executeFirstCommandTack = new Runnable() {
                            @Override
                            public void run() {
                                ServerLogger.logInfoMessage("Starting to execute a new command");
                                assert pairOfCommandAndClientAddress != null;
                                Request request = pairOfCommandAndClientAddress.getKey();
                                SocketAddress clientAddress = pairOfCommandAndClientAddress.getValue();
                                try {
                                    executeWithValidation(request, clientAddress);
                                } catch (Exception e) {
                                    ServerLogger.logErrorMessage(e.getMessage());
                                }
                                ServerLogger.logInfoMessage("Successfully executed the command command");
                            }
                        };
                        cashedThreadPool.submit(executeFirstCommandTack);
                    }
                }
            }
        };
        cashedThreadPool.submit(startCheckingForAvailableCommandsToRun);
    }

    private void executeWithValidation(Request request, SocketAddress clientAddress) {
        if (dataManager.validateUser(request.getLogin(), request.getPassword()) || request.getCommand() instanceof RegisterCommand) {
            if (request.getCommand() instanceof PrivateAccessedStudyGroupCommand) {
                final int id = ((PrivateAccessedStudyGroupCommand) request.getCommand()).getStudyGroupId();
                if (dataManager.validateOwner(request.getLogin(), id)) {
                    queueToBeSent.add(new Pair<>(request.getCommand().execute(dataManager, historyManager, request.getLogin()), clientAddress));
                } else {
                    queueToBeSent.add(new Pair<>(new Response("You are not the owner of the object so you can't do anything with it", true), clientAddress));
                }
            } else {
                queueToBeSent.add(new Pair<>(request.getCommand().execute(dataManager, historyManager, request.getLogin()), clientAddress));
            }
        } else {
            queueToBeSent.add(new Pair<>(new Response("Invalid login or password. Command was not executed", false), clientAddress));
        }
    }
}
