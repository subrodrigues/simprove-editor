/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:42
 */

package dao.model;

import java.util.List;

public class ActionModel {
    private int id;
    private TypeModel type;
    private TypeModel category;
    private String name;
    private List<StateModel> stateConditions;
    private List<SignalModel> results;
    private TransitionModel transition;
    private String errorMessage;

    public ActionModel(int id, String name, TypeModel type, TypeModel category, List<StateModel> stateConditions, List<SignalModel> results, TransitionModel transition, String errorMessage) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.category = category;
        this.stateConditions = stateConditions;
        this.results = results;
        this.transition = transition;
        this.errorMessage = errorMessage;
    }

    public ActionModel(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public ActionModel(int actionId) {
        this.id = actionId;
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

    public void addStateCondition(StateModel condition){
        if(!this.stateConditions.contains(condition)) this.stateConditions.add(condition);
    }

    public void removeStateCondition(StateModel condition){
        this.stateConditions.remove(condition);
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!ActionModel.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final ActionModel other = (ActionModel) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
