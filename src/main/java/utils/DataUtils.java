/*
 * Created by Filipe André Rodrigues on 14-03-2019 15:46
 */

package utils;

import dao.model.SignalModel;
import dao.model.SignalTemplateModel;
import dao.model.TypeModel;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DataUtils {

    private static String STRING_DELIMITER = "\\s#\\s";
    private static String PHYSICAL_OPTIONS_DELIMITER = ",\\s";

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

    public static List<TypeModel> getActionTypesFromResourceURL(URL fileUrl) throws IOException, URISyntaxException {
        Path path = Paths.get(fileUrl.toURI());
        Scanner scanner = new Scanner(path);

        List<TypeModel> actionTypesList = new ArrayList<TypeModel>();
        //read line by line
        while(scanner.hasNextLine()){
            //process each line
            int id = scanner.nextInt();
            String content = scanner.nextLine().trim();

            actionTypesList.add(new TypeModel(id, 0, content));
        }
        scanner.close();

        return actionTypesList;
    }

    public static List<SignalTemplateModel> getSignalsFromResourceURL(URL fileUrl) throws IOException, URISyntaxException {
        Path path = Paths.get(fileUrl.toURI());
        Scanner scanner = new Scanner(path);

        List<SignalTemplateModel> signalsList = new ArrayList<SignalTemplateModel>();

//        scanner.nextLine(); scanner.nextLine(); scanner.nextLine(); // Clean readme lines
        //read line by line
        while(scanner.hasNextLine()){
            //process each line
            String content = scanner.nextLine().trim();

            // If dont start with the ID, continue to next line
            if(!Character.isDigit(content.charAt(0))) continue;

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
                    String[] physicalOptions = tempArray[3].split(PHYSICAL_OPTIONS_DELIMITER);
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
        }
        scanner.close();

        return signalsList;
    }
}
