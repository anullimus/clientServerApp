package serverLogic.db;

import command.InfoCommand;
import data.initial.Semester;
import data.initial.StudyGroup;
import data.initial.User;
import serverLogic.executing.ServerLogger;
import util.DataManager;
import util.SHA512Encryptor;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DataManagerImpl implements DataManager {
    private final Database database;
    private TreeSet<StudyGroup> mainData = new TreeSet<>();
    private TreeSet<User> users = new TreeSet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);


    public DataManagerImpl(Database database) {
        this.database = database;
    }

    @Override
    public void addUser(User user) {
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            final User encryptedUser = new User(user.getId(), SHA512Encryptor.encryptThisString(user.getPassword()), user.getName());

            final int generatedId;

            try {
                generatedId = database.getUsersTable().add(encryptedUser);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            encryptedUser.setId(generatedId);
            users.add(encryptedUser);

            ServerLogger.logInfoMessage("Successfully registered a new user: " + encryptedUser);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void addStudyGroup(StudyGroup studyGroup) {
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            final int generatedId = database.getStudyGroupTable().add(studyGroup);
            studyGroup.setId(generatedId);
            mainData.add(studyGroup);
            ServerLogger.logInfoMessage("Successfully added a study group: " + studyGroup);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean validateUser(String username, String password) {
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            return users.stream().anyMatch(it -> it.getName().equals(username) && it.getPassword().equals(SHA512Encryptor.encryptThisString(password)));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean checkIfUsernameUnique(String username) {
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            return users.stream().noneMatch(it -> it.getName().matches(username));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean checkIfMin(StudyGroup studyGroup) {
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            return mainData.isEmpty() || studyGroup.compareTo(mainData.first()) < 0;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void clearOwnedData(String username) {
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            database.getStudyGroupTable().clearOwnedData(username);
            mainData.removeIf(it -> it.getAuthorName().equals(username));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            writeLock.unlock();
        }

    }

    @Override
    public String filterLessThanSemesterEnumToString(Semester inpEnum) {
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            StringJoiner output = new StringJoiner("\n\n");
            mainData.stream().filter(it -> it.getSemesterEnum().compareTo(inpEnum) < 0).forEach(it -> output.add(it.toString()));
            return output.toString();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public InfoCommand.InfoCommandResult getInfoAboutCollections() {
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            if (mainData.isEmpty()) {
                return new InfoCommand.InfoCommandResult(0, 0);
            }
            if (mainData.first().getStudentsCount() == null) {
                return new InfoCommand.InfoCommandResult(mainData.size(), 0);
            }
            return new InfoCommand.InfoCommandResult(mainData.size(), mainData.first().getStudentsCount());
        } finally {
            readLock.unlock();
        }
    }


    @Override
    public StudyGroup getMinByIdGroup() {
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            return mainData.stream().min(Comparator.comparingInt(StudyGroup::getId)).orElse(null);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String ascendingDataToString() {
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            return mainData.toString();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void removeStudyGroupById(int id) {
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            database.getStudyGroupTable().removeById(id);
            mainData.removeIf(it -> it.getId() == id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String showSortedByName() {
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            return mainData.stream().sorted(Comparator.comparing(StudyGroup::getName)).collect(Collectors.toList()).toString();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void updateStudyGroupById(int id, StudyGroup studyGroup) {
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            database.getStudyGroupTable().updateById(id, studyGroup);
            mainData.removeIf(it -> it.getId() == id);
            mainData.add(studyGroup);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removeGreaterIfOwned(StudyGroup studyGroup, String username) {
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            final Set<Integer> idsToRemove =
                    mainData
                            .tailSet(studyGroup)
                            .stream()
                            .filter(it -> it.getAuthorName().equals(username))
                            .map(StudyGroup::getId)
                            .collect(Collectors.toSet());
            for (int id : idsToRemove) {
                database.getStudyGroupTable().removeById(id);
                mainData.removeIf(it -> idsToRemove.contains(it.getId()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean validateOwner(String username, int studyGroupId) {
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            return mainData.stream().anyMatch(it -> it.getId() == studyGroupId && it.getAuthorName().equals(username));
        } finally {
            readLock.unlock();
        }
    }


    @Override
    public void initialiseData() {
        try {
            this.mainData = database.getStudyGroupTable().getCollection();
            this.users = database.getUsersTable().getCollection();
            ServerLogger.logInfoMessage("Made a data manager with initialised collections:\n"
                    + mainData + "\n\n" + users);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
