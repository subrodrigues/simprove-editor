/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:40
 */

package ui.scenario;

import dao.ScenarioDAO;
import dao.model.ScenarioModel;
import dao.model.StateModel;
import events.ScenarioEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ScenarioPresenter {
    private ScenarioUIView mView;
    private ScenarioDAO mDAO;

    private ScenarioModel mScenario = null;

    public ScenarioPresenter(ScenarioUIView view) {
        this.mView = view;

        EventBus.getDefault().register(this);
        mDAO = new ScenarioDAO();

        requestScenarioById(0);
    }

    private void requestScenarioById(int id){
        this.mDAO.requestScenarioById(id);
    }

    /*******************************************************************************************************************
     * DAO Events                                                                                               *
     *******************************************************************************************************************/

    /**
     * Receives a Scenario Event from the DAO
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScenarioEvent event) {
        if(this.mView == null) return;

        if (event.isSuccess()) {
            this.mScenario = event.getScenario();
            this.mView.updateScenarioData(this.mScenario);
        } else if (event.isNetworkError()) {
            this.mView.hideLoading();
            this.mView.showConnectionError();
        } else {
            this.mView.hideLoading();
            this.mView.showGenericErrorView();
        }
    };

    /*******************************************************************************************************************
     * View Requests                                                                                              *
     *******************************************************************************************************************/

    /**
     * Method that requests the presenter to launch the Edit State view
     *
     * @param stateId
     */
    void requestLaunchStateEditView(int stateId) {
        int stateIndex = this.mScenario.getStates().indexOf(new StateModel(stateId));

        if(stateIndex != -1){
            this.mView.showStateEditDialog(this.mScenario.getStates().get(stateIndex), this.mScenario.getStates());
        }
    }

    /**
     * Method that receives a StateModel to be updated at the DB
     *
     * @param stateToUpdate
     */
    void requestStateUpdate(StateModel stateToUpdate){
        //TODO: request DAO to update this state

        int indexToUpdate = this.mScenario.getStates().indexOf(stateToUpdate);

        if(indexToUpdate != -1){
            this.mScenario.getStates().set(indexToUpdate, stateToUpdate);
            this.mView.updateStateViewItem(indexToUpdate, stateToUpdate);
        }
    }
}
