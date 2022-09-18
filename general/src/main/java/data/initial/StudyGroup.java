package data.initial;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;


public class StudyGroup implements Comparable<StudyGroup>, Serializable {

    private final String name; //not null, not empty
    private final Coordinates coordinates; //not null
    private final LocalDate creationDate; //not null, automatic generation
    private final Integer studentsCount; // >0, null-able
    private final FormOfEducation formOfEducation; //not null
    private final Semester semesterEnum; // null-able
    private final Person groupAdmin; //not null
    private int id; // >0, unique, automatic generation
    private final String authorName;

    //CHECKSTYLE:OFF
    public StudyGroup(String name, Coordinates coordinates, Integer studentsCount, FormOfEducation formOfEducation,
                      Semester semesterEnum, Person groupAdmin, LocalDate creationDate, String authorName) {
        this.name = name;
        this.coordinates = coordinates;
        this.studentsCount = studentsCount;
        this.formOfEducation = formOfEducation;
        this.semesterEnum = semesterEnum;
        this.groupAdmin = groupAdmin;
        this.creationDate = creationDate;
        this.authorName = authorName;
    }

    //CHECKSTYLE:ON
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public FormOfEducation getFormOfEducation() {
        return formOfEducation;
    }

    public Person getGroupAdmin() {
        return groupAdmin;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "StudyGroup:"
                + "name='" + name + '\''
                + ", coordinates=" + coordinates
                + ", creationDate=" + creationDate
                + ", studentsCount=" + studentsCount
                + ", formOfEducation=" + formOfEducation
                + ", semesterEnum=" + semesterEnum
                + ", groupAdmin=" + groupAdmin
                + ", id=" + id
                + ", authorName='" + authorName + '\''
                + "}";
    }

    public Semester getSemesterEnum() {
        return semesterEnum;
    }

    public Integer getStudentsCount() {
        return studentsCount;
    }

    @Override
    public int compareTo(StudyGroup o) {
        Integer oValue = o.getStudentsCount();
        Integer thisValue = this.getStudentsCount();

        // null handling
        if (oValue == null) {
            oValue = -1;
        }
        if (thisValue == null) {
            thisValue = -1;
        }

        if (oValue - thisValue != 0) {
            return thisValue - oValue;
        } else {
            return this.getId() - o.getId();
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getAuthorName() {
        return authorName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StudyGroup that = (StudyGroup) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(coordinates, that.coordinates) && Objects.equals(creationDate, that.creationDate) && Objects.equals(studentsCount, that.studentsCount) && formOfEducation == that.formOfEducation && semesterEnum == that.semesterEnum && Objects.equals(groupAdmin, that.groupAdmin) && Objects.equals(authorName, that.authorName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, coordinates, creationDate, studentsCount, formOfEducation, semesterEnum, groupAdmin, id, authorName);
    }
}
