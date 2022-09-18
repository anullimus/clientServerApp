package command;

import util.Response;
import util.DataManager;
import util.HistoryManager;

import java.io.Serializable;


public abstract class Command implements Serializable {
    private final String name;

    protected Command(String name) {
        this.name = name;
    }

    public abstract Response execute(DataManager dataManager, HistoryManager historyManager, String username);

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Command{"
                + "name='" + name + '\''
                + '}';
    }
}
