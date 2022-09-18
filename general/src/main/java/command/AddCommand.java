package command;

import data.initial.StudyGroup;
import util.Response;
import util.DataManager;
import util.HistoryManager;

public class AddCommand extends Command {
    private final StudyGroup arg;

    public AddCommand(StudyGroup arg) {
        super("add");
        this.arg = arg;
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());
        StudyGroup studyGroup = arg;
        studyGroup.setId(-1);
        dataManager.addStudyGroup(studyGroup);
        return new Response("The element was added successfully", true);
    }
}
