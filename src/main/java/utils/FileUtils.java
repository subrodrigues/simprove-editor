/*
 * Created by Filipe André Rodrigues on 28-05-2019 21:49
 */

package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.model.ActionModel;
import dao.model.ScenarioModel;
import dao.model.SignalModel;
import dao.model.StateModel;

import java.io.*;
import java.util.Date;
import java.util.List;

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

    public static JsonObject getScenarioJSONObject(ScenarioModel scenarioModel){
        // create the albums object
        JsonObject scenario = new JsonObject();

        scenario.addProperty("name", scenarioModel.getName());
        scenario.addProperty("score", 0);
        scenario.addProperty("minScore", 0);
        scenario.addProperty("briefing", scenarioModel.getBriefing());

        // create an array to hold states
        JsonArray states = new JsonArray();

        for(StateModel stateModel: scenarioModel.getStates()){
            JsonObject state = new JsonObject();

            state.addProperty("id", stateModel.getId());
            state.addProperty("name", stateModel.getName());

            JsonArray signals = getSignalsJSONArray(stateModel.getSignals());
            state.add("signals", signals);

            JsonArray tips = new JsonArray();
            state.add("tips", tips);

            JsonObject transition = null;
            if(stateModel.getTransition() != null) {
                transition = new JsonObject();
                transition.addProperty("id", stateModel.getTransition().getId());
                transition.addProperty("duration", stateModel.getTransition().getDuration());
                transition.addProperty("stateId", stateModel.getTransition().getStateId());
            }
            state.add("transition", transition);

            states.add(state);
        }
        scenario.add("states", states);


        // create an array to hold states
        JsonArray actions = new JsonArray();
        for(ActionModel actionModel: scenarioModel.getActions()){
            JsonObject action = new JsonObject();

            action.addProperty("id", actionModel.getId());
            action.addProperty("name", actionModel.getName());

            JsonObject type = new JsonObject();
            type.addProperty("id", actionModel.getCategory().getTypeId());
            type.addProperty("name", actionModel.getCategory().getName());
            action.add("type", type);

            action.addProperty("behavior", actionModel.getBehavior());

            action.addProperty("effectTime", actionModel.getEffectTime());

            action.addProperty("usageLimit", actionModel.getUsageLimit());

            action.addProperty("is_complement", actionModel.getIsComplement());
            JsonArray conditions = new JsonArray();
            for(StateModel stateModel: actionModel.getStateConditions()){
                conditions.add(stateModel.getId());
            }
            action.add("conditions", conditions);


            JsonArray resultSignals = getSignalsJSONArray(actionModel.getResults());
            action.add("results", resultSignals);

            JsonObject transition = null;
            if(actionModel.getTransition() != null) {
                transition = new JsonObject();
                transition.addProperty("id", actionModel.getTransition().getId());
                transition.addProperty("stateId", actionModel.getTransition().getStateId());
            }
            action.add("transition", transition);

            actions.add(action);
        }
        scenario.add("actions", actions);

        return scenario;
    }

    private static JsonArray getSignalsJSONArray(List<SignalModel> signalModels) {
        JsonArray signals = new JsonArray();

        for(SignalModel signalModel: signalModels){
            JsonObject signal = new JsonObject();
            signal.addProperty("id", signalModel.getId());
            signal.addProperty("name", signalModel.getName());
            signal.addProperty("value", signalModel.getValue());

            signals.add(signal);
        }

        return signals;
    }

    public static void saveScenarioJSONFile(String absolutePath, ScenarioModel scenario) throws IOException {
        JsonObject scenarioJson = getScenarioJSONObject(scenario);

        // try-with-resources statement based on post comment below :)
        try (FileWriter file = new FileWriter(absolutePath)) {
            file.write(scenarioJson.toString());
        }
    }
}
