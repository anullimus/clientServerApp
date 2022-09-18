package data;

import java.io.FileWriter;
import java.io.IOException;

public class scriptCreator {
    public static void main(String[] args) {
        try(FileWriter writer = new FileWriter("script.txt", false))
        {
            for(int i = 0; i < 30000; i++){

                String command = "add";
                writer.write(command + "\n");

                String name = "TestName";
                writer.write(name + i + "\n");

                String studentsCount = "1";
                writer.write(studentsCount + "\n");

                String formOfEducation = "DISTANCE_EDUCATION";
                writer.write(formOfEducation + "\n");

                String semesterEnum = "THIRD";
                writer.write(semesterEnum + "\n");

                String x = "1";
                writer.write(x + "\n");

                String y = "1";
                writer.write(y + "\n");

                String PersonName = "TestPersonName";
                writer.write(PersonName + "\n");

                String height = "1";
                writer.write(height + "\n");

                String nationality = "RUSSIA";
                writer.write(nationality + "\n");

                String locName = "TestLocName";
                writer.write(locName + "\n");

                String xNew = "1";
                writer.write(xNew + "\n");

                String yNew = "1";
                writer.write(yNew + "\n");

                writer.flush();
            }
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }
}
