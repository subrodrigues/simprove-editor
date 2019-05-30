/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:40
 */

package ui.scenario;

import dao.ScenarioDAO;
import dao.model.*;
import events.dao.ActionTypesEvent;
import events.dao.ScenarioEvent;
import events.ui.SideMenuEvent;
import events.dao.SignalTypesEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.*;

public class ScenarioPresenter {
    private ScenarioUIView mView;
    private ScenarioDAO mDAO;

    private List<TypeModel> actionTypes = null;
    private List<SignalTemplateModel> signalTypes = null;

    private ScenarioModel mScenario = null;

    private int mCurrentSelectedStateItem = -1;
    private Set<Integer> selectedActions = new HashSet<>();

    public ScenarioPresenter(ScenarioUIView view) {
        this.mView = view;

        EventBus.getDefault().register(this);
        mDAO = new ScenarioDAO();

        actionTypes = new ArrayList<TypeModel>();
        signalTypes = new ArrayList<SignalTemplateModel>();
        mDAO.requestDefaultActionTypes();
        mDAO.requestDefaultSignals();

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

    /**
     * Receives a Scenario Event from the DAO
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionTypesEvent event) {
        if(this.mView == null) return;

        if (event.isSuccess()) {
            this.actionTypes = event.getActionTypes();
        } else if (event.isNetworkError()) {
            // TODO: deal with it
        } else {
            // TODO: deal with it
        }
    };

    /**
     * Receives a Scenario Event from the DAO
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SignalTypesEvent event) {
        if(this.mView == null) return;

        if (event.isSuccess()) {
            this.signalTypes = event.getSignalTypes();
        } else if (event.isNetworkError()) {
            // TODO: deal with it
        } else {
            // TODO: deal with it
        }
    };

    /**
     * Aux method to clean the currently selected card.
     */
    private void deselectSelectedPane() {
        // We start by deselecting the current card
        if(this.mCurrentSelectedStateItem != -1){
            this.mView.deselectStateViewItem(this.mCurrentSelectedStateItem);
            this.mCurrentSelectedStateItem = -1;
        }
    }

    /**
     * Method that JUST deals with a request to highlight a specific state view card UI.
     *
     * @param stateId
     */
    void showSelectedStateUIItem(int stateId) {

        // Acquire index to highlight
        int selectedIndex = this.mScenario.getStates().indexOf(new StateModel(stateId));

        // If the new selected card is not a previously selected one
        if(selectedIndex != -1) {
            this.mView.selectStateViewItem(selectedIndex);
        }
    }

    /**
     * Method that just deals with a request to highlight a specific action view card UI.
     *
     * @param actionId
     */
    void showSelectedActionUIItem(int actionId) {

        // Acquire index to highlight
        int selectedIndex = this.mScenario.getActions().indexOf(new ActionModel(actionId));

        // If the new selected card is not a previously selected one
        if(selectedIndex != -1) {
            this.mView.selectActionViewItem(selectedIndex);
        }
    }

    /**
     * Receives a Scenario Event from the DAO
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SideMenuEvent event) {
        if(this.mView == null) return;

        if (event.isSuccess()) {
            switch (event.getSideMenuOption()){
                case NEW_SCENARIO:
                    this.requestScenarioReset();
                    break;
                case SAVE_SCENARIO:
                    String defaultFilename = this.mScenario.getName();

                    if (defaultFilename.isEmpty()) {
                        Date date = new Date();
                        defaultFilename = "new_scenario_" + date.getTime();
                    }
                    this.mView.requestSaveScenarioDialog(defaultFilename);
                    break;
                case LOAD_SCENARIO:
                    this.mView.requestLoadScenarioDialog();
                    break;
                case EXPORT_SCENARIO:
                    break;
            }

        } else if (event.isNetworkError()) {
            // TODO: deal with it
        } else {
            // TODO: deal with it
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
            //deselectSelectedPane();
            this.mView.showStateEditDialog(this.mScenario.getStates().get(stateIndex), this.mScenario.getStates(), this.signalTypes);
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
            this.mView.updateStateViewItem(indexToUpdate, stateToUpdate, stateToUpdate.getId() == this.mCurrentSelectedStateItem);

            this.mView.updateStatesGraphView(this.mScenario.getStates());
        }
    }

    /**
     * Method that receives a State id to be deleted.
     *
     * @param stateId
     */
    void requestDeleteStateById(int stateId) {
        //TODO: request DAO to delete this state

        int indexToRemove = this.mScenario.getStates().indexOf(new StateModel(stateId));

        if(indexToRemove != -1) {
            this.mScenario.getStates().remove(indexToRemove);
            this.mView.removeStateViewItem(indexToRemove);
            this.cleanSelectedActions();

            this.mView.updateStatesGraphView(this.mScenario.getStates());
        }
    }

    /**
     * Method that requests the presenter to launch the New State view
     *
     */
    void requestLaunchNewStateView() {
        this.mView.showNewStateDialog(this.mScenario.getStates(), this.signalTypes);
    }


    /**
     * Method that requests the presenter to launch the New Action view
     *
     */
    void requestLaunchNewActionView() {
        this.mView.showNewActionDialog(this.mScenario.getActions(), this.mScenario.getStates(), this.actionTypes, this.signalTypes);
    }

    /**
     * Method that receives a StateModel to be created at the DB
     *
     * @param newStateModel
     */
    void requestStateCreation(StateModel newStateModel) {
        this.mScenario.getStates().add(newStateModel);
        this.mView.addStateViewItem(newStateModel);

        this.mView.updateStatesGraphView(this.mScenario.getStates());

    }

    /**
     * Method that deals with a request to highlight a specific state view card.
     *
     * It checks the currently selected one, in order to deselect.
     *
     * @param stateId
     */
    void requestSelectStateItem(int stateId) {
        // We start by deselecting the current card
        if(this.mCurrentSelectedStateItem != -1){
            this.mView.deselectStateViewItem(this.mCurrentSelectedStateItem);
            cleanSelectedActions();
        }

        // Acquire index to highlight
        int indexToHighlight = this.mScenario.getStates().indexOf(new StateModel(stateId));

        // If the new selected card is not the previously selected one
        if(indexToHighlight != this.mCurrentSelectedStateItem && indexToHighlight != -1) {
            this.mView.selectStateViewItem(indexToHighlight);

            for(ActionModel action: this.mScenario.getActions()){
                if(action.getStateConditions().contains(new StateModel(stateId))){
                    this.selectedActions.add(action.getId());
                    this.showSelectedActionUIItem(action.getId());
                }
            }
        }

        // Update the currently selected flag
        if(indexToHighlight != this.mCurrentSelectedStateItem)
            this.mCurrentSelectedStateItem = indexToHighlight;
        else // If we just deselected a previously selected card
            this.mCurrentSelectedStateItem = -1;
    }

    /**
     * Method that cleans all the selected action card items from the UI
     */
    private void cleanSelectedActions() {
        for(int actionId: this.selectedActions){
            int indexToClean = this.mScenario.getActions().indexOf(new ActionModel(actionId));

            if(indexToClean != -1){
                this.mView.deselectActionViewItem(indexToClean);
            }
        }
        this.selectedActions.clear();
    }

    /**
     * Method that deals with a request to highlight a specific action view card and updates the DB.
     *
     * It checks the currently selected one, in order to deselect.
     *
     * @param actionId
     */
    void requestSelectActionItem(int actionId) {
        // We start by deselecting the current card
        if(this.mCurrentSelectedStateItem == -1){
            return; // TODO: Show warning
        }

        // Acquire index to highlight
        int selectedIndex = this.mScenario.getActions().indexOf(new ActionModel(actionId));

        boolean isToRemove = false;
        // If the new selected card is not a previously selected one
        if(!selectedActions.contains(actionId) && selectedIndex != -1) {
            this.mView.selectActionViewItem(selectedIndex);

            // Update the currently selected Items and DB
            this.selectedActions.add(actionId);
        } else{
            isToRemove = true;
            this.mView.deselectActionViewItem(selectedIndex);

            // Update the currently selected Items and DB
            this.selectedActions.remove(actionId);
        }

        // TODO: Sync DB
        if(isToRemove){
            this.mScenario.getActions().get(selectedIndex).removeStateCondition(this.mScenario.getStates().get(this.mCurrentSelectedStateItem));
        } else {
            this.mScenario.getActions().get(selectedIndex).addStateCondition(this.mScenario.getStates().get(this.mCurrentSelectedStateItem));
        }
    }

    /**
     * Method that requests the presenter to launch the Edit Action view
     *
     * @param actionId
     */
    void requestLaunchActionEditView(int actionId) {
        int index = this.mScenario.getActions().indexOf(new ActionModel(actionId));

        if(index != -1){
//            deselectSelectedPane();
            this.mView.showActionEditDialog(this.mScenario.getActions().get(index), this.mScenario.getStates(),  this.actionTypes, this.signalTypes);
        }
    }

    /**
     * Method that receives a ActionModel to be updated at the DB
     *
     * @param actionToUpdate
     */
    void requestActionUpdate(ActionModel actionToUpdate){
        //TODO: request DAO to update this state

        int indexToUpdate = this.mScenario.getActions().indexOf(actionToUpdate);

        if(indexToUpdate != -1){
            this.mScenario.getActions().set(indexToUpdate, actionToUpdate);
            this.mView.updateActionViewItem(indexToUpdate, actionToUpdate, selectedActions.contains(actionToUpdate.getId()));
        }
    }

    /**
     * Method that receives an Action id to be deleted.
     *
     * @param actionId
     */
    void requestDeleteActionById(int actionId) {
        //TODO: request DAO to delete this state

        int indexToRemove = this.mScenario.getActions().indexOf(new ActionModel(actionId));

        if(indexToRemove != -1) {
            this.mScenario.getActions().remove(indexToRemove);
            this.mView.removeActionViewItem(indexToRemove);
//            this.cleanSelectedActions();
        }
    }

    /**
     * Method that receives a ActionModel to be created at the DB
     *
     * @param newActionModel
     */
    void requestActionCreation(ActionModel newActionModel) {
        this.mScenario.getActions().add(newActionModel);
        this.mView.addActionViewItem(newActionModel);
    }

    void requestScenarioReset(){
        this.mCurrentSelectedStateItem = -1;
        this.mScenario = new ScenarioModel();
        this.mView.updateScenarioData(this.mScenario);
    }

    public void requestOpenScenarioFile(String absolutePath) {
        this.mDAO.requestScenarioByAbsolutePath(absolutePath);
    }

    public void requestSaveScenarioWithPath(String absolutePath){
        this.mDAO.saveCurrentScenarioWithPath(absolutePath, this.mScenario);

    }
}
