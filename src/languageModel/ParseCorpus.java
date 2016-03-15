package languageModel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * A class that allows a user to select the file they want to parse,
 * via  a GUI. The selected file is then processed concurrently into n-grams
 * which are then stored concurrently into a ConcurrentHashMap.
 *
 * @author Shaquille Momoh
 */
public class ParseCorpus {
    /**
     * chooser allows users to select a file by navigating through
     * directories
     */
    private static JFileChooser chooser = new JFileChooser(System.getProperties().getProperty("user.dir"));

    /**
     * Read a file into a string.
     *
     * @return returns a string containing all text in the file.
     */
    public static String readFile(Scanner input) {
        return input.useDelimiter("\\Z").next();
    }

    /**
     * A method to allow user to input the value of n, for generating n-grams,
     * via  GUI.
     *
     * @return n : he value of n, for generating n-grams.
     */
    public static Integer getN() {
        String input = JOptionPane.showInputDialog("Enter N:", "3");
        Integer n = Integer.parseInt(input);
        return n;
    }

    /**
     * Brings up chooser for user to select a file or  a directory to be parsed.
     *
     * @return : The file or directory that was chosen.
     * @throws IOException
     */
    public File getCorpus() throws IOException {
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Select a File or a Directory as your Corpus");
        int retval = chooser.showOpenDialog(null);

        if (retval == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            return f;
        }

        return null;

    }

    /**
     * A method used to process the corpus into n-grams concurrently.
     *
     * @return The processed n-grams stored in a ConcurrentHashMap
     * @throws IOException, InterruptedException
     */
    public ConcurrentHashMap<String, Integer> processFiles(File f, int n) throws IOException, InterruptedException, ExecutionException {

        ConcurrentHashMap<String, Integer> languageModel = new ConcurrentHashMap<>();
        if (f.isDirectory()) {

            File[] filesInDirectory = chooser.getSelectedFile().listFiles();

            // Removing the 'DS.store' file generated by OS X.
            if (System.getProperty("os.name").equals("Mac OS X")) {
                filesInDirectory = Arrays.copyOfRange(filesInDirectory, 1, filesInDirectory.length);
            }
            for (int i = 0; i < filesInDirectory.length; i++) {
                File file = filesInDirectory[i];
                if (file.isFile()) {
                    Split split = new Split(file, languageModel, n);
                    int noThreads = split.getThreadNumber();
                    long chunks = split.getChunkNumber(noThreads);
                    split.processAll(noThreads, chunks);
                }

            }
            return languageModel;
        } else {
            Split split = new Split(f, languageModel, n);
            int noThreads = split.getThreadNumber();
            long chunks = split.getChunkNumber(noThreads);
            split.processAll(noThreads, chunks);
            return languageModel;
        }

    }
}
