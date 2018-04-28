package parsingWebsite.fileWriter;

import java.io.BufferedWriter;
import java.io.IOException;

public class MyFileWriter {
    public void writeToFile(String filename, String content) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new java.io.FileWriter(filename, true));
            writer.write(content + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
