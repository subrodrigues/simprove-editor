/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:41
 */

package dao;

import dao.model.*;
import events.ScenarioEvent;
import org.greenrobot.eventbus.EventBus;

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
     *
     */
    public void requestScenarioById(int scenarioId) {
        //TODO: Database

        // TODO: DELETE THIS MOCKED DATA

        /** MOCKED STATES **/
        List<StateModel> mockedStates = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            List<SignalModel> signals = new ArrayList<SignalModel>();
            signals.add(new SignalModel(0, new SignalTypeModel(0, 1), "Heart Rate", 120f));
            List<TipModel> tips = new ArrayList<TipModel>();

            mockedStates.add(new StateModel(i, "State " + i, new TypeModel(0, 1, "Tipo Cenas"),
                    signals, new TransitionModel(0, 10, 1), tips));
        }


        /** MOCKED ACTIONS **/
        List<ActionModel> mockedActions = new ArrayList<>();
        for(int i = 10; i < 26; i++) {
            List<SignalModel> results = new ArrayList<SignalModel>();
            results.add(new SignalModel(1, new SignalTypeModel(0, 1), "Heart Rate", 120f));
            List<StateModel> stateConditions = new ArrayList<StateModel>();

            mockedActions.add(new ActionModel(i, "Action " + i, new TypeModel(0, 1, "Tipo Coiso"),
                    new TypeModel(1, 1, "Diagnosis"),
                    stateConditions,
                    results,
                    new TransitionModel(0, 10, 1),
                    "Oops, you screwed it."));
        }

        ScenarioModel mockedScenario = new ScenarioModel(0, "Mocked Data",
                "First comes the Universe, then comes the prayer.",
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
}
