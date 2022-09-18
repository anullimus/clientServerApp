package command;


import util.Response;
import util.DataManager;
import util.HistoryManager;

/**
 * Небольшое уточнение: команды {@link ClearCommand}, {@link RemoveGreaterCommand} выполнятся всегда,
 * а вот команды {@link RemoveByIdCommand}, {@link UpdateCommand} не выполнятся, если
 * клиент пытается взаимодействовать с объектами, которые ему не принадлежат
 * именно поэтому clear, remove greater не имплементируют {@link PrivateAccessedStudyGroupCommand}
 */
public class ClearCommand extends Command {
    public ClearCommand() {
        super("clear");
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());
        dataManager.clearOwnedData(username);
        return new Response("The data you owned was cleared successfully.", true);
    }
}
