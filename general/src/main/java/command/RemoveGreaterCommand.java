package command;

import data.initial.StudyGroup;
import util.Response;
import util.DataManager;
import util.HistoryManager;

/**
 * Небольшое уточнение: команды {@link ClearCommand}, {@link RemoveGreaterCommand} выполнятся всегда,
 * а вот команды {@link RemoveByIdCommand}, {@link UpdateCommand} не выполнятся, если
 * клиент пытается взаимодействовать с объектами, которые ему не принадлежат
 * именно поэтому clear, remove greater не имплементируют {@link PrivateAccessedStudyGroupCommand}
 */
public class RemoveGreaterCommand extends Command {
    private final StudyGroup arg;

    public RemoveGreaterCommand(StudyGroup arg) {
        super("remove_greater");
        this.arg = arg;
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());
        dataManager.removeGreaterIfOwned(arg, username);
        return new Response("Successfully removed greater elements", true);
    }
}
