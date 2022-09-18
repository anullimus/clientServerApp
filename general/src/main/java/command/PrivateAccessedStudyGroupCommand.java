package command;

/**
 * A mark that indicates that this command should only be executed
 * if the executor is the owner of a study group
 */
public interface PrivateAccessedStudyGroupCommand {
    int getStudyGroupId();
}
