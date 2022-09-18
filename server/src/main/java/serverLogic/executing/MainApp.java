package serverLogic.executing;


import javafx.util.Pair;
import serverLogic.connection.ClientDataReceiver;
import serverLogic.connection.ClientDataSender;
import serverLogic.util.HistoryManagerImpl;
import util.DataManager;
import util.Request;
import util.Response;
import util.State;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class MainApp {
    private final Queue<Pair<Request, SocketAddress>> queueToBeExecuted;
    private final Queue<Pair<Response, SocketAddress>> queueToBeSent;
    private final int port;
    private final String ip;
    private final CommandHandler commandHandler;
    private final ClientDataReceiver clientDataReceiver;
    private final ExecutorService cashedThreadPool;
    private final ExecutorService fixedThreadPool;

    public MainApp(int port, String ip, ExecutorService cashedThreadPool, ExecutorService fixedThreadPool,
                   DataManager dataManager) {
        this.ip = ip;
        this.port = port;
        queueToBeExecuted = new LinkedBlockingQueue<>();
        queueToBeSent = new LinkedBlockingQueue<>();
        this.commandHandler = new CommandHandler(queueToBeExecuted, queueToBeSent, dataManager, new HistoryManagerImpl());
        this.clientDataReceiver = new ClientDataReceiver(queueToBeExecuted);
        this.cashedThreadPool = cashedThreadPool;
        this.fixedThreadPool = fixedThreadPool;
    }

    public void start(State<Boolean> isWorking) throws IOException {
        try (DatagramChannel datagramChannel = DatagramChannel.open()) {
            datagramChannel.bind(new InetSocketAddress(ip, port));
            datagramChannel.configureBlocking(false);

            cashedThreadPool.submit(() -> {
                try {
                    clientDataReceiver.startReceivingData(datagramChannel, isWorking);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            commandHandler.startToHandleCommands(
                    isWorking,
                    cashedThreadPool
            );

            while (isWorking.getValue()) {
                if (!queueToBeSent.isEmpty()) {
                    Pair<Response, SocketAddress> commandResultDtoAndSocketAddress = queueToBeSent.poll();
                    fixedThreadPool.submit(new ClientDataSender(commandResultDtoAndSocketAddress.getKey(), datagramChannel,
                            commandResultDtoAndSocketAddress.getValue()));
                }
            }

        } catch (BindException e) {
            ServerLogger.logErrorMessage("Could not start the server, bind exception. Please, use another port.");
            isWorking.setValue(false);
        }
    }
}
