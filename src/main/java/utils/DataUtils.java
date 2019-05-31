/*
 * Created by Filipe André Rodrigues on 14-03-2019 15:46
 */

package utils;

import dao.model.SignalTemplateModel;
import dao.model.TypeModel;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DataUtils {

    private static String STRING_DELIMITER = "\\s#\\s";
    private static String CSV_DELIMITER = ",";

    private static List<String> readUsingScanner(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        Scanner scanner = new Scanner(path);

        List<String> lines = new ArrayList<String>();
        //read line by line
        while(scanner.hasNextLine()){
            //process each line
            String line = scanner.nextLine();
            lines.add(line);
        }
        scanner.close();

        return lines;
    }

    public static List<TypeModel> getActionTypesFromResourceURL(FileInputStream pathIs) throws IOException, URISyntaxException {
        List<TypeModel> actionTypesList = new ArrayList<TypeModel>();

        InputStreamReader isr = new InputStreamReader( pathIs, "UTF-8" );
        BufferedReader br = new BufferedReader( isr );
        String line = br.readLine();
        while( line != null ) {
            // Process each line
            String[] tempArray = line.split(CSV_DELIMITER);
            actionTypesList.add(new TypeModel(Integer.valueOf(tempArray[0]), 0, tempArray[1]));

            line = br.readLine();
        }
        br.close();
        isr.close();
        pathIs.close();

        return actionTypesList;
    }

    public static List<SignalTemplateModel> getSignalsFromResourceURL(FileInputStream pathIs) throws IOException, URISyntaxException {
        List<SignalTemplateModel> signalsList = new ArrayList<SignalTemplateModel>();

        InputStreamReader isr = new InputStreamReader( pathIs, "UTF-8" );
        BufferedReader br = new BufferedReader( isr );
        String line = br.readLine();
        while( line != null ) {
            // process lines of text
            String content = line.trim();

            // If dont start with the ID, continue to next line
            if(!Character.isDigit(content.charAt(0))){
                line = br.readLine();
                continue;
            }

            String[] tempArray = content.split(STRING_DELIMITER);

            int type = Integer.valueOf(tempArray[1]);
            switch(type){
                case 0:
                    signalsList.add(new SignalTemplateModel(Integer.valueOf(tempArray[0]),
                            type,
                            tempArray[2],
                            tempArray[3],
                            Integer.valueOf(tempArray[4]),
                            Integer.valueOf(tempArray[5]),
                            Float.valueOf(tempArray[6]),
                            null
                    ));
                    break;
                case 1:
                    String[] physicalOptions = tempArray[3].split(CSV_DELIMITER);
                    signalsList.add(new SignalTemplateModel(Integer.valueOf(tempArray[0]),
                            type,
                            tempArray[2],
                            null,
                            -1,
                            -1,
                            .0f,
                            Arrays.asList(physicalOptions)
                    ));
                    break;
            }

            line = br.readLine();
        }
        br.close();
        isr.close();
        pathIs.close();

        return signalsList;
    }
}
