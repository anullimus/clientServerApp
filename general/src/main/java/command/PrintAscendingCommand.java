package command;


import util.Response;
import util.DataManager;
import util.HistoryManager;

public class PrintAscendingCommand extends Command {

    public PrintAscendingCommand() {
        super("print_ascending");
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());
        return new Response(dataManager.ascendingDataToString(), true);
    }
}
