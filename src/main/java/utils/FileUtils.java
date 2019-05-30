/*
 * Created by Filipe André Rodrigues on 28-05-2019 21:49
 */

package utils;

import dao.model.ScenarioModel;

import java.io.*;
import java.util.Date;

public class FileUtils {
    private static String SAVE_PATH = "scenarios/";
    private static String SAVE_FILETYPE = ".bin";

    private static String getSavePath(String filename) {
        return SAVE_PATH.concat(filename).concat(SAVE_FILETYPE);
    }

    // TODO: Event system to deal with errors
    public static ScenarioModel loadScenarioModel(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream
                = new FileInputStream("scenarios/" + filename + ".bin");
        ObjectInputStream objectInputStream
                = new ObjectInputStream(fileInputStream);

        ScenarioModel scenario = (ScenarioModel) objectInputStream.readObject();
        objectInputStream.close();

        return scenario;
    }

    // TODO: Event system to deal with errors
    public static void saveScenarioModel(ScenarioModel scenario) throws IOException, ClassNotFoundException {
        FileOutputStream fileOutputStream = null;

        String filename = scenario.getName();

        if (filename.isEmpty()) {
            Date date = new Date();
            filename = "new_scenario_" + date.getTime();
        }

        fileOutputStream = new FileOutputStream("scenarios/" + filename + ".bin");

        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(scenario);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public static ScenarioModel loadScenarioModelByAbsolutePath(String path) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream
                = new FileInputStream(path);
        ObjectInputStream objectInputStream
                = new ObjectInputStream(fileInputStream);

        ScenarioModel scenario = (ScenarioModel) objectInputStream.readObject();
        objectInputStream.close();

        return scenario;
    }

    public static void saveScenarioModel(String absolutePath, ScenarioModel scenario) throws IOException {
        FileOutputStream fileOutputStream = null;

        fileOutputStream = new FileOutputStream(absolutePath);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(scenario);
        objectOutputStream.flush();
        objectOutputStream.close();
    }
}
