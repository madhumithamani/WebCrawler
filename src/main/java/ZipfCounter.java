import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class ZipfCounter {
    private static HashMap<String, Integer> termFrequencyTracker = new HashMap<String, Integer>();

    public static void countFrquency(String filePath){
        FileReader fileReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        String[] tokens;

        try {
            fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            while(line!=null){
                tokens = line.split(" ");
                for(String token:tokens) {
                    if(Pattern.matches("[a-zA-Z0-9]*",token)){
                    //if (!token.matches("^[a-zA-Z0-9'-]*")) {}
                    //else{
                        if (termFrequencyTracker.containsKey(token)) {
                            termFrequencyTracker.put(token, termFrequencyTracker.get(token) + 1);
                        } else
                            termFrequencyTracker.put(token, 1);
                    }
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            displayAndWriteToFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(fileReader!=null){
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void displayAndWriteToFile(){
        if(createNewDocument("/Users/MadVish/Documents/zipfoutput.csv"))
        {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter("/Users/MadVish/Documents/zipfoutput.csv");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                for(Map.Entry<String,Integer> entry: termFrequencyTracker.entrySet()){
                    bufferedWriter.write(entry.getKey() + "," + entry.getValue());
                    bufferedWriter.write("\n");
                    bufferedWriter.flush();
                    System.out.println(entry.getKey() + "::" + entry.getValue());
                }
                bufferedWriter.close();
///                System.out.println("Content saved successfully");
            }catch(IOException ex){
                ex.printStackTrace();
            } finally {
                if(fileWriter  != null)
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    private static boolean createNewDocument(String localURL){
        File file = new File(localURL);
        // if file does not exists, then create it
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return true;
    }

    public static void main(String[] args) {
        ZipfCounter.countFrquency("/Users/MadVish/Documents/WSIR/coen272/projectsamples/output.txt");
    }
}
