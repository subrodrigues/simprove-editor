/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:40
 */

package ui.scenario;

import dao.ScenarioDAO;
import dao.model.*;
import events.dao.*;
import events.ui.SideMenuEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import utils.FileUtils;

import java.util.*;

public class ScenarioPresenter {
    private ScenarioUIView mView;
    private ScenarioDAO mDAO;

    private List<TypeModel> mActionTypes = null;
    private List<TypeModel> mActionCategories = null;
    private List<TypeModel> mActionSubCategories = null;
    private List<TypeModel> mActorTypes = null;
    private List<TypeModel> mScenarioSettingTypes = null;
    private List<SignalTemplateModel> mSignalTypes = null;

    private ScenarioModel mScenario = null;

    private int mCurrentSelectedStateItem = -1;
    private Set<Integer> selectedActions = new HashSet<>();

    public ScenarioPresenter(ScenarioUIView view) {
        this.mView = view;

        EventBus.getDefault().register(this);
        mDAO = new ScenarioDAO();

        mActionTypes = new ArrayList<TypeModel>();
        mSignalTypes = new ArrayList<SignalTemplateModel>();
        mDAO.requestDefaultActionTypes();
        mDAO.requestDefaultActionCategories();
        mDAO.requestDefaultActionSubCategories();
        mDAO.requestDefaultScenarioSettings();
        mDAO.requestDefaultActorTypes();
        mDAO.requestDefaultSignals();

        requestDemoScenario();
    }

    private void requestDemoScenario(){
        this.requestOpenScenarioFile("./scenarios/Demo Scenario.bin");
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
            this.mActionTypes = event.getActionTypes();
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
    public void onMessageEvent(ActionCategoriesEvent event) {
        if(this.mView == null) return;

        if (event.isSuccess()) {
            this.mActionCategories = event.getActionCategories();
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
    public void onMessageEvent(ActionSubCategoriesEvent event) {
        if(this.mView == null) return;

        if (event.isSuccess()) {
            this.mActionSubCategories = event.getActionSubCategories();
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
            this.mSignalTypes = event.getSignalTypes();
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
    public void onMessageEvent(ActorTypesEvent event) {
        if(this.mView == null) return;

        if (event.isSuccess()) {
            this.mActorTypes = event.getActorTypes();

            this.mView.updateActorErrorMsg(this.mActorTypes);
        } else if (event.isNetworkError()) {
            // TODO: deal with it
        } else {
            // TODO: deal with it
        }
    };

    /**
     * Receives a Scenario Setting Event from the DAO
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScenarioSettingTypesEvent event) {
        if(this.mView == null) return;

        if (event.isSuccess()) {
            this.mScenarioSettingTypes = event.getScenarioSettingTypes();

            this.mView.updateScenarioSettingTypes(this.mScenarioSettingTypes);
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
                    String defaultFilename = getScenarioFilename();
                    this.mView.requestSaveScenarioDialog(defaultFilename);
                    break;
                case LOAD_SCENARIO:
                    this.mView.requestLoadScenarioDialog();
                    break;
                case EXPORT_SCENARIO:
                    this.requestJsonExport();
                    defaultFilename = getScenarioFilename();

                    this.mView.requestExportJSONScenario(defaultFilename);
                    break;
            }

        } else if (event.isNetworkError()) {
            // TODO: deal with it
        } else {
            // TODO: deal with it
        }
    }

    /**
     * Method that returns a filename default value, from the scenario name.
     *
     * @return scenario file name
     */
    private String getScenarioFilename() {
        String defaultFilename = this.mScenario.getName();

        if (defaultFilename.isEmpty()) {
            Date date = new Date();
            defaultFilename = "new_scenario_" + date.getTime();
        }
        return defaultFilename;
    }


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
            this.mView.showStateEditDialog(this.mScenario.getStates().get(stateIndex), this.mScenario.getStates(), this.mSignalTypes, this.mActorTypes, this.mScenario.getActions());
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

            this.mView.updateGraphView(this.mScenario.getStates(), this.mScenario.getActions());
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

            this.mView.updateGraphView(this.mScenario.getStates(), this.mScenario.getActions());
        }
    }

    /**
     * Method that requests the presenter to launch the New State view
     *
     */
    void requestLaunchNewStateView() {
        this.mView.showNewStateDialog(this.mScenario.getStates(), this.mSignalTypes, this.mActorTypes, this.mScenario.getActions());
    }

    /**
     * Method that requests the presenter to launch the New Action view
     *
     */
    void requestLaunchNewActionView() {
        this.mView.showNewActionDialog(this.mScenario.getActions(), this.mScenario.getStates(),
                this.mActionTypes, this.mActionCategories,
                this.mActionSubCategories, this.mSignalTypes, this.mActorTypes);
    }

    /**
     * Method that receives a StateModel to be created at the DB
     *
     * @param newStateModel
     */
    void requestStateCreation(StateModel newStateModel) {
        this.mScenario.getStates().add(newStateModel);
        this.mView.addStateViewItem(newStateModel);

        this.mView.updateGraphView(this.mScenario.getStates(), this.mScenario.getActions());

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
            this.mView.showActionEditDialog(this.mScenario.getActions().get(index), this.mScenario.getActions(), this.mScenario.getStates(),
                    this.mActionTypes, this.mActionCategories, this.mActionSubCategories, this.mSignalTypes, this.mActorTypes);
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

            this.mView.updateGraphView(this.mScenario.getStates(), this.mScenario.getActions());
        }
    }

    /**
     * Method that updates the Scenario Actions after a change on complementary dependencies
     *
     * @param actionToChange
     * @param previousCompActions
     * @param actionsToUpdate
     */
    void requestComplementaryActionsUpdate(ActionModel actionToChange, List<ActionModel> previousCompActions, List<ActionModel> actionsToUpdate) {
        /** We start by removing the broken relations **/
        for(ActionModel previous: previousCompActions){
            if(!actionsToUpdate.contains(previous)){ // If a previous action relation has been removed
                int index = this.mScenario.getActions().indexOf(previous);

                ActionModel act = this.mScenario.getActions().get(index);
                act.removeComplementaryAction(actionToChange);

                this.mView.updateActionViewItem(index, act, selectedActions.contains(act.getId()));

            }
        }

        /** Then we update the scenario actions with the new relation **/
        for(ActionModel action: actionsToUpdate){
            int index = this.mScenario.getActions().indexOf(action);

            ActionModel act = this.mScenario.getActions().get(index);
            act.addComplementaryAction(actionToChange);

            this.mView.updateActionViewItem(index, act, selectedActions.contains(act.getId()));
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

            this.mView.updateGraphView(this.mScenario.getStates(), this.mScenario.getActions());
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

        this.mView.updateGraphView(this.mScenario.getStates(), this.mScenario.getActions());
    }

    void requestScenarioReset(){
        this.mCurrentSelectedStateItem = -1;
        this.mScenario = new ScenarioModel();
        this.mView.updateScenarioData(this.mScenario);
    }

    private void requestJsonExport() {
        FileUtils.getScenarioJSONObject(mScenario);
    }

    void requestOpenScenarioFile(String absolutePath) {
        this.mDAO.requestScenarioByAbsolutePath(absolutePath);
    }

    void requestSaveScenarioWithPath(String absolutePath){
        this.mDAO.saveCurrentScenarioWithPath(absolutePath, this.mScenario);

    }

    void updateScenarioName(String name) {
        this.mScenario.setName(name);
    }

    void updateBriefingContent(String briefing) {
        this.mScenario.setBriefing(briefing);
    }

    void updateScoreContent(String score) {
        this.mScenario.setScore(Integer.valueOf(score));
    }

    void updateMinScoreContent(String score) {
        this.mScenario.setMinScore(Integer.valueOf(score));
    }

    void requestExportJSONWithPath(String absolutePath) {
        this.mDAO.saveJSONFileWithPath(absolutePath, this.mScenario);
    }

    void updateGeneralErrorMsg(String errorMsg) {
        this.mScenario.setDefaultErrorMessage(errorMsg);
    }


    void updateGeneralErrorMsgActor(TypeModel newActor) {
        this.mScenario.setActorDefaultErrorMessage(newActor);
    }

    public void requestRefreshGraphView() {
        this.mView.updateGraphView(this.mScenario.getStates(), this.mScenario.getActions());
    }
}
