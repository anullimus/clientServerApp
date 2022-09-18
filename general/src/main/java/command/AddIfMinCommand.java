package command;

import data.initial.StudyGroup;
import util.Response;
import util.DataManager;
import util.HistoryManager;


public class AddIfMinCommand extends Command {
    private final StudyGroup arg;

    public AddIfMinCommand(StudyGroup arg) {
        super("add_if_min");
        this.arg = arg;
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());
        StudyGroup studyGroup = arg;
        if (dataManager.checkIfMin(studyGroup)) {
            dataManager.addStudyGroup(studyGroup);
            return new Response("The element was added successfully", true);
        } else {
            return new Response("The element was not min, so it was not added", true);
        }
    }
}
