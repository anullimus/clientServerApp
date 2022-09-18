package command;


import util.Response;
import util.DataManager;
import util.HistoryManager;

public class HistoryCommand extends Command {
    public HistoryCommand() {
        super("history");
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());
        return new Response(historyManager.niceToString(), true);
    }
}
