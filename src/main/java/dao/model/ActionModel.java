/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:42
 */

package dao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ActionModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private TypeModel type;
    private TypeModel category;
    private TypeModel subCategory;
    private float effectTime;
    private int usageLimit;
    private int isComplement;
    private String behavior;
    private String name;
    private List<StateModel> stateConditions;
    private List<SignalModel> results;
    private TransitionModel transition;
    private String errorMessage;
    private ScoreModel score;

    public ActionModel(int id, String name, TypeModel type, TypeModel category, TypeModel subCategory,
                       int effectTime, int usageLimit, int isComplement, String behavior,
                       List<StateModel> stateConditions, List<SignalModel> results,
                       TransitionModel transition, String errorMessage, ScoreModel score) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.category = category;
        this.subCategory = subCategory;
        this.effectTime = effectTime;
        this.usageLimit = usageLimit;
        this.isComplement = isComplement;
        this.behavior = behavior;
        this.stateConditions = stateConditions;
        this.results = results;
        this.transition = transition;
        this.errorMessage = errorMessage;
        this.score = score;
    }

    public ActionModel(int id, String name) {
        this.id = id;
        this.name = name;
        this.score = new ScoreModel(id);

        this.stateConditions = new ArrayList<>();
    }


    public ActionModel(int id) {
        this.id = id;
        this.score = new ScoreModel(id);

        this.stateConditions = new ArrayList<>();
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


    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public int getIsComplement() {
        return isComplement;
    }

    public void setIsComplement(int isComplement) {
        this.isComplement = isComplement;
    }

    public int getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(int effectTime) {
        this.effectTime = effectTime;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    public ScoreModel getScore() {
        return score;
    }

    public void setScore(ScoreModel score) {
        this.score = score;
    }

    public TypeModel getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(TypeModel subCategory) {
        this.subCategory = subCategory;
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
