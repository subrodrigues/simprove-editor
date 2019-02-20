/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:42
 */

package dao.model;

import java.util.List;

public class ActionModel {
    private int id;
    private TypeModel type;

    public ActionModel(int id, TypeModel type, TypeModel category, String name, List<StateModel> stateConditions, List<SignalModel> results, TransitionModel transition, String errorMessage) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.name = name;
        this.stateConditions = stateConditions;
        this.results = results;
        this.transition = transition;
        this.errorMessage = errorMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeModel getType() {
        return type;
    }

    public void setType(TypeModel type) {
        this.type = type;
    }

    public TypeModel getCategory() {
        return category;
    }

    public void setCategory(TypeModel category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StateModel> getStateConditions() {
        return stateConditions;
    }

    public void setStateConditions(List<StateModel> stateConditions) {
        this.stateConditions = stateConditions;
    }

    public List<SignalModel> getResults() {
        return results;
    }

    public void setResults(List<SignalModel> results) {
        this.results = results;
    }

    public TransitionModel getTransition() {
        return transition;
    }

    public void setTransition(TransitionModel transition) {
        this.transition = transition;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private TypeModel category;
    private String name;
    private List<StateModel> stateConditions;
    private List<SignalModel> results;
    private TransitionModel transition;
    private String errorMessage;

}
