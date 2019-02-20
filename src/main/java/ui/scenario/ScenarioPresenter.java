/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:40
 */

package ui.scenario;

import dao.ScenarioDAO;
import events.ScenarioEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ScenarioPresenter {
    private ScenarioUIController mView;
    private ScenarioDAO mDAO;

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
            this.mView.updateScenarioData(event.getScenario());
        } else if (event.isNetworkError()) {
            this.mView.hideLoading();
            this.mView.showConnectionError();
        } else {
            this.mView.hideLoading();
            this.mView.showGenericErrorView();
        }
    };
}
