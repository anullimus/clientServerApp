package command;


import util.Response;
import util.DataManager;
import util.HistoryManager;

import java.io.Serializable;

public class InfoCommand extends Command {

    public InfoCommand() {
        super("info");
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());

        return new Response(dataManager.getInfoAboutCollections(), true);
    }

    public static final class InfoCommandResult implements Serializable {
        private final int numberOfElements;
        private final int biggestStudentsCount;

        public InfoCommandResult(Integer numberOfElements, int biggestStudentsCount) {
            this.numberOfElements = numberOfElements;
            this.biggestStudentsCount = biggestStudentsCount;
        }

        @Override
        public String toString() {
            return "InfoCommandResult{"
                    + "numberOfElements='" + numberOfElements + '\''
                    + ", biggestStudentsCount=" + biggestStudentsCount
                    + '}';
        }
    }
}
