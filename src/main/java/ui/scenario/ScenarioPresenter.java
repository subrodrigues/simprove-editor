/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:40
 */

package ui.scenario;

import dao.ScenarioDAO;
import dao.model.ScenarioModel;
import events.ScenarioEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ScenarioPresenter {
    private ScenarioUIController mView;
    private ScenarioDAO mDAO;

    private ScenarioModel mScenario = null;

    public ScenarioPresenter(ScenarioUIController view) {
        this.mView = view;

        EventBus.getDefault().register(this);
        mDAO = new ScenarioDAO();

        requestScenarioById(0);
    }

    private void requestScenarioById(int id){
        this.mDAO.requestScenarioById(id);
    }

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

    /**
     * Method that requests the presenter to launch the Edit State view
     *
     * @param stateId
     */
    public void requestStateEdit(int stateId) {
        this.mView.showStateEditDialog(stateId);
    }
}
