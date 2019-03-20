/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:41
 */

package dao;

import dao.model.*;
import events.ActionTypesEvent;
import events.ScenarioEvent;
import org.greenrobot.eventbus.EventBus;
import utils.DataUtils;

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
     * Access the DAO to get the correspondent scenario model.
     * This method is async, notifying the Presenter when query is complete.
     */
    public void requestScenarioById(int scenarioId) {
        //TODO: Database

        // TODO: DELETE THIS MOCKED DATA



        // TODO: fill mocked data from: (https://jsonformatter.org/json-viewer)
        // TODO: Create the behavior of window cancel


        /** MOCKED STATES **/
        List<StateModel> mockedStates = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
        List<SignalModel> signals = new ArrayList<SignalModel>();
        signals.add(new SignalModel(0, new SignalTypeModel(-1, 1), "Heart Rate", 120f));
        List<TipModel> tips = new ArrayList<TipModel>();

        mockedStates.add(new StateModel(0, "Baseline", new TypeModel(-1, 1, "No Type"),
                signals, null, tips));

        mockedStates.add(new StateModel(1, "Responsive", new TypeModel(-1, 1, "No Type"),
                signals,  new TransitionModel(0, 180, 2), tips));

        mockedStates.add(new StateModel(2, "Mumbling", new TypeModel(-1, 1, "No Type"),
                signals, new TransitionModel(0, 120, 3), tips));

        mockedStates.add(new StateModel(3, "Anaphylactic shock", new TypeModel(-1, 1, "No Type"),
                signals, new TransitionModel(0, 120, 4), tips));

        mockedStates.add(new StateModel(4, "ICU", new TypeModel(-1, 1, "No Type"),
                signals, null, tips));

        mockedStates.add(new StateModel(5, "Treatment", new TypeModel(-1, 1, "No Type"),
                signals, new TransitionModel(0, 30, 6), tips));

        mockedStates.add(new StateModel(6, "Recovery", new TypeModel(-1, 1, "No Type"),
                signals, new TransitionModel(0, 30, 7), tips));

        mockedStates.add(new StateModel(7, "Discharge", new TypeModel(-1, 1, "No Type"),
                signals, null, tips));


//        }

        /** MOCKED ACTIONS **/
        List<ActionModel> mockedActions = new ArrayList<>();
//        for (int i = 10; i < 26; i++) {
        List<SignalModel> results = new ArrayList<SignalModel>();
        results.add(new SignalModel(1, new SignalTypeModel(-1, 1), "Heart Rate", 120f));
        List<StateModel> stateConditions = new ArrayList<StateModel>();

        mockedActions.add(new ActionModel(0, "Start", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 0, "Control"),
                stateConditions,
                results,
                new TransitionModel(0, 0, 1),
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(1, "Oximetry", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Diagnosis"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(2, "ECG", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Diagnosis"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(3, "Blood Pressure", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Diagnosis"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(4, "Epinephrine", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Procedure"),
                stateConditions,
                results,
                new TransitionModel(0, 0, 5),
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(5, "Fluids", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Procedure"),
                stateConditions,
                results,
                new TransitionModel(0, 0, 5),
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(6, "Propofol", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Procedure"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(7, "Temperature", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Diagnosis"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(8, "Desloratadine", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Procedure"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(9, "Blood Tests", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Diagnosis"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(10, "Capillary Refill", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Diagnosis"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(11, "Basic Life Support", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Diagnosis"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(12, "Oxygen", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Procedure"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(13, "Diagnosis", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Diagnosis"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(14, "Anaphylaxis Reaction", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Diagnosis"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));

        mockedActions.add(new ActionModel(15, "Pulmonary Edema", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Diagnosis"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));


        mockedActions.add(new ActionModel(16, "Hemorragic Shock", new TypeModel(-1, 1, "Tipo Coiso"),
                new TypeModel(0, 1, "Diagnosis"),
                stateConditions,
                results,
                null,
                "Oops, you screwed it."));



        ScenarioModel mockedScenario = new ScenarioModel(0, "IntraHospital",
                "A 27-year old male patient who arrived to the emergency department by ambulance with abdominal pain, vomiting, and shortness of breath shortly after eating a meal at a nearby Asian restaurant.",
                mockedStates, mockedActions);

        EventBus.getDefault().post(new ScenarioEvent(mockedScenario));

//
//         TODO API/DB
/*       getServiceAdapter().getScenario(getContext().getString(R.string.api_header_access_token, accessToken), scenarioid);
            call.enqueue(new Callback<BaseResponse<ScenarioStatus>>() {

            @Override
            public void onSuccess(BaseResponse<ScenarioStatus> response) {
                removeRequest(call);

                EventBus.getDefault().post(new ScenarioEvent(response.getData()));
            }

            @Override
            public void onFailure(ResponseError errorDetails) {
                removeRequest(call);

                EventBus.getDefault().post(new ScenarioEvent(errorDetails));
            }
        });

        addRequest(call);*/
    }

    /**
     * Method that accesses the resources and load the Action Types in the resources action data file.
     *
     * @return List<TypeModel> containing all the TypeModels saved at the local file.
     */
    public void requestDefaultActionTypes() {
        List<TypeModel> actionTypes = null;

        try {
            actionTypes = DataUtils.getActionTypesFromResourceURL(getClass().getResource("/raw/actions_data.txt"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new ActionTypesEvent(actionTypes));
    }
}
