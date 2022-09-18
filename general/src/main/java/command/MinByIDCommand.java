package command;


import data.initial.StudyGroup;
import util.Response;
import util.DataManager;
import util.HistoryManager;

public class MinByIDCommand extends Command {
    private final String arg;

    public MinByIDCommand(String arg) {
        super("min_by_id");
        this.arg = arg;
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());
        final StudyGroup minStudyGroup = dataManager.getMinByIdGroup();
        if (minStudyGroup == null) {
            return new Response("Collection is empty :(", true);
        } else {
            return new Response(minStudyGroup, true);
        }
    }
}
