/*
 * Created by Filipe André Rodrigues on 14-03-2019 15:46
 */

package utils;

import dao.model.TypeModel;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataUtils {

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
}
