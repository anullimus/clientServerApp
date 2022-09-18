package util;

import command.Command;

import java.io.Serializable;
import java.util.Objects;

public class Request implements Serializable {
    private final Command command;
    private final String login;
    private final String password;


    public Request(Command command, String login, String password) {
        this.command = command;
        this.login = login;
        this.password = password;
    }

    public Request(Command command) {
        this.command = command;
        this.login = "";
        this.password = "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Request that = (Request) o;
        return Objects.equals(command, that.command) && Objects.equals(login, that.login) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, login, password);
    }

    public Command getCommand() {
        return command;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
