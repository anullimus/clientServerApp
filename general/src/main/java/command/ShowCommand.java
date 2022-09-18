package command;

import util.Response;
import util.DataManager;
import util.HistoryManager;


public class ShowCommand extends Command {


    public ShowCommand() {
        super("show");
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());
        return new Response(dataManager.showSortedByName(), true);
    }
}
