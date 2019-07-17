/*
 * Created by Filipe André Rodrigues on 28-05-2019 21:49
 */

package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.model.*;

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
        JsonObject scenario = new JsonObject();

        scenario.addProperty("name", scenarioModel.getName());
        scenario.addProperty("score", scenarioModel.getScore());
        scenario.addProperty("minScore", scenarioModel.getMinScore());
        scenario.addProperty("briefing", scenarioModel.getBriefing());

        JsonObject errorMsg = new JsonObject();
        errorMsg.addProperty("actorType", scenarioModel.getActorDefaultErrorMessage().getName());
        errorMsg.addProperty("actorTypeId", scenarioModel.getActorDefaultErrorMessage().getTypeId());
        errorMsg.addProperty("message", scenarioModel.getDefaultErrorMessage());
        scenario.add("defaultActionError", errorMsg);

        // create an array to hold states
        JsonArray states = new JsonArray();

        for(StateModel stateModel: scenarioModel.getStates()){
            JsonObject state = new JsonObject();

            state.addProperty("id", stateModel.getId());
            state.addProperty("name", stateModel.getName());
            state.addProperty("startState", stateModel.getIsStartState());
            state.addProperty("endState", stateModel.getIsEndState());

            JsonArray signals = getSignalsJSONArray(stateModel.getSignals());
            state.add("signals", signals);

            JsonArray tips = getTipsJSONArray(stateModel.getTips());
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


        // create an array to hold actions
        JsonArray actions = new JsonArray();
        for(ActionModel actionModel: scenarioModel.getActions()){
            JsonObject action = new JsonObject();

            action.addProperty("id", actionModel.getId());
            action.addProperty("name", actionModel.getName());

            JsonObject type = new JsonObject();
            type.addProperty("id", actionModel.getType().getTypeId());
            type.addProperty("name", actionModel.getType().getName());
            action.add("type", type);


            /** ACTION CATEGORIES HAMMERED INTO AN ARRAY **/
            JsonObject category = new JsonObject();
            category.addProperty("id", actionModel.getCategory().getTypeId());
            category.addProperty("name", actionModel.getCategory().getName());
//            action.add("category", category); // TODO: Uncomment this when a proper server is implemented

            JsonObject subcategory = new JsonObject();
            subcategory.addProperty("id", actionModel.getSubCategory().getTypeId());
            subcategory.addProperty("name", actionModel.getSubCategory().getName());
//            action.add("subcategory", subcategory); // TODO: Uncomment this when a proper server is implemented

            JsonArray categories = new JsonArray();
            categories.add(category);
            categories.add(subcategory);
            action.add("categories", categories);
            /** ACTION CATEGORIES HAMMERED INTO AN ARRAY **/


            JsonArray compActions = new JsonArray();
            for(ActionModel actModel: actionModel.getComplementaryActions()){
                compActions.add(actModel.getId());
            }
            action.add("compActions", compActions);

            action.addProperty("behavior", actionModel.getBehavior());

            action.addProperty("effectTime", actionModel.getEffectTime());

            action.addProperty("usageLimit", actionModel.getUsageLimit());

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

            JsonObject score = getActionScoreJsonObj(actionModel.getScore());
            action.add("score", score);

            JsonObject actionErrorMsg = new JsonObject();
            actionErrorMsg.addProperty("actorType", actionModel.getActorErrorMessage() == null ?
                    "" : actionModel.getActorErrorMessage().getType().getName());
            actionErrorMsg.addProperty("actorTypeId", actionModel.getActorErrorMessage() == null ?
                    -1 : actionModel.getActorErrorMessage().getType().getTypeId());
            actionErrorMsg.addProperty("actorName", actionModel.getActorErrorMessage() == null ?
                    "" : actionModel.getActorErrorMessage().getName());

            actionErrorMsg.addProperty("message", actionModel.getErrorMessage());
            action.add("errorMsg", actionErrorMsg);

            actions.add(action);
        }
        scenario.add("actions", actions);

        return scenario;
    }

    private static JsonObject getActionScoreJsonObj(ScoreModel scoreModel) {
        JsonObject score = new JsonObject();
        score.addProperty("lostValue", scoreModel.getScoreLost());
        score.addProperty("lossOvertime", scoreModel.getLossOvertime());

        JsonArray startStates = new JsonArray();
        for(StateModel stateModel: scoreModel.getStartStates()){
            startStates.add(stateModel.getId());
        }
        score.add("startStates", startStates);

        JsonArray endStates = new JsonArray();
        for(StateModel stateModel: scoreModel.getEndStates()){
            endStates.add(stateModel.getId());
        }
        score.add("endStates", endStates);

        return score;
    }

    private static JsonArray getTipsJSONArray(List<TipModel> tipModels) {
        JsonArray tips = new JsonArray();

        for(TipModel tipModel: tipModels){
            JsonObject tip = new JsonObject();
            tip.addProperty("id", tipModel.getId());
            tip.addProperty("message", tipModel.getMessage());
            tip.addProperty("actorName", tipModel.getActor() == null ? "" : tipModel.getActor().getName());
            tip.addProperty("actorType", tipModel.getActor() == null ? "" : tipModel.getActor().getType().getName());
            tip.addProperty("actorTypeId", tipModel.getActor() == null ? -1 : tipModel.getActor().getType().getTypeId());
            tip.addProperty("activationTime", tipModel.getActivationTime());
            tip.addProperty("duration", tipModel.getDuration());

            JsonArray actionsDone = new JsonArray();
            for(ActionModel action: tipModel.getActionsDone()){
                actionsDone.add(action.getId());
            }
            tip.add("actionsDone", actionsDone);

            JsonArray actionsTodo = new JsonArray();
            for(ActionModel action: tipModel.getActionsTodo()){
                actionsTodo.add(action.getId());
            }
            tip.add("actionsNotDone", actionsTodo);

            tips.add(tip);
        }

        return tips;
    }

    private static JsonArray getSignalsJSONArray(List<SignalModel> signalModels) {
        JsonArray signals = new JsonArray();

        for(SignalModel signalModel: signalModels){
            JsonObject signal = new JsonObject();
            signal.addProperty("id", signalModel.getId());
            signal.addProperty("signalTemplateId", signalModel.getTemplate().getId());
            signal.addProperty("type", signalModel.getType());
            signal.addProperty("name", signalModel.getName());
            signal.addProperty("value", signalModel.getValue());

            JsonArray plotYValues = new JsonArray();
            if(signalModel.getPlotYValue() != null) {
                for (Integer yVal : signalModel.getPlotYValue()) {
                    plotYValues.add(yVal);
                }
            }
            signal.add("plotYValues", plotYValues);

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
