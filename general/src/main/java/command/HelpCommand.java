package command;


import util.Response;
import util.DataManager;
import util.HistoryManager;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public Response execute(DataManager dataManager, HistoryManager historyManager, String username) {
        historyManager.addNote(this.getName());
        // stream api would not help
        return new Response(
                "help : gives information about available commands\n"
                        + "info : gives information about collection\n"
                        + "show : shows every element in collection with string\n"
                        + "add {element} : adds new element to collection\n"
                        + "update id {element} : update element info by it's id\n"
                        + "remove_by_id id : delete element by it's id\n"
                        + "clear : clears collection\n"
                        + "execute_script file_name : executes script entered in a file\n"
                        + "exit : exits the program (!!!does not save data!!!)\n"
                        + "add_if_min {element} : adds new element to the collection if it's value less than min element's value\n"
                        + "remove_greater {element} : deletes every element in the collection with value more than entered element's value\n"
                        + "history : shows last 6 command usages\n"
                        + "min_by_id : gives information about a random element from collection with minimum value\n"
                        + "filter_less_than_semester_enum semesterEnum : shows every element with semesterEnum value less than entered\n"
                        + "print_ascending : prints every element in ascending order", true);
    }
}
