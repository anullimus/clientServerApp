package serverLogic.connection;


import javafx.util.Pair;
import serverLogic.executing.ServerLogger;
import util.Request;
import util.State;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

public class ClientDataReceiver {
    private static final int HEADER_LENGTH = 4;
    private static final int TIMEOUT_MILLS = 5;
    private final Queue<Pair<Request, SocketAddress>> queueToBeExecuted;

    public ClientDataReceiver(Queue<Pair<Request, SocketAddress>> queueToBeExecuted) {
        this.queueToBeExecuted = queueToBeExecuted;
    }

    public static int bytesToInt(byte[] bytes) {
        final int vosem = 8;
        final int ff = 0xFF;

        int value = 0;
        for (byte b : bytes) {
            value = (value << vosem) + (b & ff);
        }
        return value;
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    public void startReceivingData(DatagramChannel datagramChannel, State<Boolean> isWorking) throws IOException, InterruptedException {
        while (isWorking.getValue()) {
            ByteBuffer amountOfBytesInMainDataBuffer = ByteBuffer.wrap(new byte[HEADER_LENGTH]);
            receiveActiveWaiting(datagramChannel, amountOfBytesInMainDataBuffer, isWorking);
            if (isWorking.getValue()) {
                ByteBuffer dataByteBuffer = ByteBuffer.wrap(new byte[bytesToInt(amountOfBytesInMainDataBuffer.array())]);
                SocketAddress clientSocketAddress = null;
                try {
                    clientSocketAddress = receiveWithTimeout(datagramChannel, dataByteBuffer, TIMEOUT_MILLS);
                } catch (TimeoutException e) {
                    ServerLogger.logErrorMessage("Couldn't receive correct information from client");
                }
                Request receivedRequest;
                try {
                    receivedRequest = ((Request) deserialize(dataByteBuffer.array()));
                    Pair<Request, SocketAddress> pairToBeExecuted = new Pair<>(receivedRequest, clientSocketAddress);
                    queueToBeExecuted.add(pairToBeExecuted);

                    ServerLogger.logInfoMessage("Received a full request from a client, added it to an executing queue:\n" + pairToBeExecuted);
                } catch (ClassNotFoundException e) {
                    ServerLogger.logErrorMessage("Found incorrect data from client. Ignoring it");
                }

            }
        }
    }

    private SocketAddress receiveWithTimeout(DatagramChannel datagramChannel, ByteBuffer byteBuffer, int timeoutMills) throws IOException, InterruptedException, TimeoutException {
        int amountToWait = timeoutMills;
        SocketAddress receivedSocketAddress;
        while (amountToWait > 0) {
            receivedSocketAddress = datagramChannel.receive(byteBuffer);
            if (Objects.nonNull(receivedSocketAddress)) {
                ServerLogger.logInfoMessage("Received a new client request 2/2");
                return receivedSocketAddress;
            } else {
                Thread.sleep(1);
                amountToWait--;
            }
        }
        throw new TimeoutException();
    }

    private SocketAddress receiveActiveWaiting(DatagramChannel datagramChannel, ByteBuffer byteBuffer, State<Boolean> isWorking) throws IOException {
        while (isWorking.getValue()) {
            SocketAddress receivedSocketAddress = datagramChannel.receive(byteBuffer);
            if (Objects.nonNull(receivedSocketAddress)) {
                ServerLogger.logInfoMessage("Received a new client request 1/2");
                return receivedSocketAddress;
            }
        }
        return null;
    }
}
