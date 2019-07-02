/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:41
 */

package dao;

import base.ErrorType;
import base.ResponseError;
import dao.model.*;
import events.dao.*;
import org.greenrobot.eventbus.EventBus;
import utils.ReadDataUtils;
import utils.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * ScenarioDAO
 * Class that implements the Repository Pattern (DAO).
 * Isolates the data layer, providing data from multiple sources (DB, API).
 *
 * In a near future this class can be adapted, in order to receive a default getData() that make API requests and returns local saved data.
 *
 *  TODO: deal with errors properly
 *
 */
public class ScenarioDAO {

    /**
     * Method that accesses the resources and load the Action Types in the resources action data file.
     *
     * @return List<TypeModel> containing all the TypeModels saved at the local file.
     */
    public void requestDefaultActionTypes() {
        List<TypeModel> actionTypes = null;

        try {
            actionTypes = ReadDataUtils.getDataTypesFromResourceURL(new FileInputStream("raw/action_types.csv"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new ActionTypesEvent(actionTypes));
    }

    /**
     * Method that accesses the resources and load the Action Categories in the resources action data file.
     *
     * @return List<TypeModel> containing all the TypeModels saved at the local file.
     */
    public void requestDefaultActionCategories() {
        List<TypeModel> actionCategories = null;

        try {
            actionCategories = ReadDataUtils.getDataTypesFromResourceURL(new FileInputStream("raw/action_categories.csv"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new ActionCategoriesEvent(actionCategories));
    }

    /**
     * Method that accesses the resources and load the Action Subcategories in the resources action data file.
     *
     * @return List<TypeModel> containing all the TypeModels saved at the local file.
     */
    public void requestDefaultActionSubCategories() {
        List<TypeModel> actionSubCategories = null;

        try {
            actionSubCategories = ReadDataUtils.getDataTypesFromResourceURL(new FileInputStream("raw/action_subcategories.csv"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new ActionSubCategoriesEvent(actionSubCategories));
    }

    /**
     * Method that accesses the resources and load the SignalTemplateModel in the resources action data file.
     *
     * @return List<SignalTemplateModel> containing all the SignalTemplateModel saved at the local file.
     */
    public void requestDefaultSignals() {
        List<SignalTemplateModel> signals = new ArrayList<>();

        try {
            signals = ReadDataUtils.getSignalsFromResourceURL(new FileInputStream("raw/signals_data.csv"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new SignalTypesEvent(signals));
    }

    /**
     * Method that saves the current ScenarioModel to a file.
     * This is a temporary feature using serialization instead of a DB
     */
    public void saveCurrentScenario(ScenarioModel scenario){
        try {
            FileUtils.saveScenarioModel(scenario);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // TODO: Deal with thiss
        }
    }

    /**
     * Method that saves the current ScenarioModel to a file.
     * This is a temporary feature using serialization instead of a DB
     */
    public void requestScenarioByFilename(String filename){
        try {
           ScenarioModel scenario = FileUtils.loadScenarioModel(filename);

           EventBus.getDefault().post(scenario);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // TODO: Deal with thiss
        }
    }

    /**
     * Method that saves the current ScenarioModel to a file.
     * This is a temporary feature using serialization instead of a DB
     */
    public void requestScenarioByAbsolutePath(String path){
        try {
            ScenarioModel scenario = FileUtils.loadScenarioModelByAbsolutePath(path);

            EventBus.getDefault().post(new ScenarioEvent(scenario));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // TODO: Deal with this

            EventBus.getDefault().post(new ScenarioEvent(new ResponseError(ErrorType.GENERIC)));
        }
    }

    /**
     * Method that saves the current ScenarioModel to a file, with an absolute path.
     *
     * This is a temporary feature using serialization instead of a DB
     */
    public void saveCurrentScenarioWithPath(String absolutePath, ScenarioModel scenario) {
        try {
            FileUtils.saveScenarioModel(absolutePath, scenario);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Deal with this
        }
    }

    /**
     * Method that exports the scenario into the JSON file from the protocol
     *
     * @param absolutePath File absolute path (include name) to disk
     * @param scenario Data to be appended to the file
     */
    public void saveJSONFileWithPath(String absolutePath, ScenarioModel scenario) {
        try {
            FileUtils.saveScenarioJSONFile(absolutePath, scenario);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Deal with this
        }
    }

    /**
     * Method that accesses the resources and load the Actor Types in the resources actor data file.
     *
     * @return List<TypeModel> containing all the TypeModels saved at the local file.
     */
    public void requestDefaultActorTypes() {
        List<TypeModel> actorTypes = null;

        try {
            actorTypes = ReadDataUtils.getDataTypesFromResourceURL(new FileInputStream("raw/actor_types.csv"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new ActorTypesEvent(actorTypes));
    }
}
