package duke.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import duke.exceptions.DukeException;
import duke.tasks.Deadline;
import duke.tasks.Event;
import duke.tasks.Task;
import duke.tasks.ToDo;

/**
 * Storage class that contains file for data to be read from.
 */
public class Storage {
    private File storageFile;
    public Storage(String filePath) {
        this.storageFile = makeFile(filePath);
    }

    /**
     * Creates file at the specified file path if it does not exist.
     */
    private File makeFile(String filePath) {
        File f = new File(filePath);
        f.getParentFile().mkdirs();
        try {
            f.createNewFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return f;
    }

    //Solution below adapted from https://stackoverflow.com/questions/1377279/find-a-line-in-a-file-and-remove-it

    /**
     * Deletes a line from storage text file.
     * @param line
     */
    private void deleteLine(String line) {
        File tempFile = new File("temp.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.storageFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String rmLine = line;
            String currLine;
            boolean done = false;
            boolean linesLeft = true;
            while (linesLeft) {
                currLine = reader.readLine();
                if (currLine == null) {
                    linesLeft = false;
                } else {
                    String trimmedLine = currLine.trim();
                    if (trimmedLine.equals(rmLine) && !done) {
                        done = true;
                        continue;
                    }
                    writer.write(currLine + "\n");
                }
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempFile.renameTo(this.storageFile);
    }

    /**
     * Modifies a line in storage text file.
     * @param line Line to be modified.
     * @param newLine Line to be modified into.
     */
    private void modifyLine(String line, String newLine) {
        File tempFile = new File("temp.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.storageFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String rmLine = line;
            String currLine;
            boolean done = false;
            boolean linesLeft = true;
            while (linesLeft) {
                currLine = reader.readLine();
                if (currLine == null) {
                    linesLeft = false;
                } else {
                    String trimmedLine = currLine.trim();
                    if (trimmedLine.equals(rmLine) && !done) {
                        done = true;
                        writer.write(newLine + "\n");
                    } else {
                        writer.write(currLine + "\n");
                    }
                }
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempFile.renameTo(this.storageFile);
    }

    /**
     * Add a line of text to storage text file.
     * @param text Line of text to be added.
     */
    private void appendToFile(String text) {
        try {
            FileWriter fw = new FileWriter(this.storageFile, true); // create a FileWriter in append mode
            fw.write(text + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads list of tasks from storage text file.
     */
    public ArrayList<Task> load() throws DukeException {
        ArrayList<Task> userTasks = new ArrayList<>();
        if (this.storageFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.storageFile));

                String currLine;
                boolean linesLeft = true;
                while (linesLeft) {
                    currLine = reader.readLine();
                    if (currLine == null) {
                        linesLeft = false;
                    } else {
                        String[] fields = currLine.split(Pattern.quote(" | "));
                        if (fields[0].equals("T")) {
                            Task t = new ToDo(fields[2]);
                            if (fields[1].equals("1")) {
                                t.markAsDone();
                            }
                            userTasks.add(t);
                        } else if (fields[0].equals("D")) {
                            Task t = new Deadline(fields[2], fields[3]);
                            if (fields[1].equals("1")) {
                                t.markAsDone();
                            }
                            userTasks.add(t);
                        } else if (fields[0].equals("E")) {
                            Task t = new Event(fields[2], fields[3], fields[4]);
                            if (fields[1].equals("1")) {
                                t.markAsDone();
                            }
                            userTasks.add(t);
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                throw new DukeException("Error loading file");
            }
            return userTasks;
        } else {
            return userTasks;
        }
    }

    /**
     * Adds a task to storage text file.
     */
    public void addTask(String taskText) {
        appendToFile(taskText);
    }

    /**
     * Modifies a task in storage text file.
     */
    public void modifyTask(String oldText, String newText) {
        modifyLine(oldText, newText);
    }

    /**
     * Deletes a task in storage text file.
     */
    public void deleteTask(String taskText) {
        deleteLine(taskText);
    }
}
