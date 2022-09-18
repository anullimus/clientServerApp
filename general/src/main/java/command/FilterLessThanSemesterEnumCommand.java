package command;


import data.initial.Semester;
import util.Response;
import util.DataManager;
import util.HistoryManager;

public class FilterLessThanSemesterEnumCommand extends Command {
    private final String arg;

    public FilterLessThanSemesterEnumCommand(String arg) {
        super("filter_less_than_semester_enum");
        this.arg = arg;
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());
        Semester inpEnum;
        try {
            if ("null".equals(arg)) {
                inpEnum = Semester.THIRD;
            } else {
                inpEnum = Semester.valueOf(arg);
            }
        } catch (IllegalArgumentException e) {
            return new Response("Your argument was incorrect", true);
        }
        return new Response(dataManager.filterLessThanSemesterEnumToString(inpEnum), true);

    }
}
