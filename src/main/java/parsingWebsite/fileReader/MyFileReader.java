package parsingWebsite.fileReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MyFileReader {

    public String readFromFile(String filename) {

        StringBuilder results = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(new java.io.File(filename))); // if you have an existing file, "new java.io" is omitted
            String line = null;

            while ((line = reader.readLine()) != null) {
                results.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return results.toString();
    }
}
