package clientLogic.util;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

public class InputManager {
        private final Scanner scanner;
    private final Stack<BufferedReader> currentFilesReaders = new Stack<>();
    private final Stack<File> currentFiles = new Stack<>();

    public InputManager(InputStream inputStream) {
        this.scanner = new Scanner(inputStream);
    }


    public String nextLine() throws IOException {
        try {
            if (!currentFilesReaders.isEmpty()) {
                String input = currentFilesReaders.peek().readLine();
                if (input == null) {
                    currentFiles.pop();
                    currentFilesReaders.pop().close();
                    return nextLine();
                } else {
                    return input;
                }
            } else {
                return scanner.nextLine();
            }
        }catch (NoSuchElementException noSuchElementException){
            System.out.println("Goodbye.");
            System.exit(0);
        } return null;
    }

    public void connectToFile(File file) throws IOException, UnsupportedOperationException {
        if (currentFiles.contains(file)) {
            throw new UnsupportedOperationException("The file was not executed due to recursion");
        } else {
            BufferedReader newReader = new BufferedReader(new FileReader(file));
            currentFiles.push(file);
            currentFilesReaders.push(newReader);
        }
    }

}
