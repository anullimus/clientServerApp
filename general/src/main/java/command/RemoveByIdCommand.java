package command;

import util.Response;
import util.DataManager;
import util.HistoryManager;

public class RemoveByIdCommand extends Command implements PrivateAccessedStudyGroupCommand {
    private final String arg;

    public RemoveByIdCommand(String arg) {
        super("remove_by_id");
        this.arg = arg;
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());

        int intArg;
        try {
            intArg = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return new Response("Your argument was incorrect. The command was not executed.", true);
        }

        dataManager.removeStudyGroupById(intArg);

        return new Response("The element was removed if it was in the data", true);
    }

    @Override
    public int getStudyGroupId() {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
