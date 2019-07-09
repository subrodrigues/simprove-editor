/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:42
 */

package dao.model;

import java.io.Serializable;
import java.util.List;

public class StateModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private TypeModel type;
    private List<SignalModel> signals;
    private TransitionModel transition;
    private List<TipModel> tips;
    private int isStartState;
    private int isEndState;

    public StateModel(int id) {
        this.id = id;
    }

    public StateModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public StateModel(int id, String name, TypeModel type, List<SignalModel> signals,
                      TransitionModel transition, List<TipModel> tips, int isStartState, int isEndState) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.signals = signals;
        this.transition = transition;
        this.tips = tips;
        this.isStartState = isStartState;
        this.isEndState = isEndState;
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

    public List<SignalModel> getSignals() {
        return signals;
    }

    public void setSignals(List<SignalModel> signals) {
        this.signals = signals;
    }

    public TransitionModel getTransition() {
        return transition;
    }

    public void setTransition(TransitionModel transition) {
        this.transition = transition;
    }

    public List<TipModel> getTips() {
        return tips;
    }

    public void setTips(List<TipModel> tips) {
        this.tips = tips;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsStartState() {
        return isStartState;
    }

    public void setIsStartState(int isStartState) {
        this.isStartState = isStartState;
    }

    public int getIsEndState() {
        return isEndState;
    }

    public void setIsEndState(int isEndState) {
        this.isEndState = isEndState;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!StateModel.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final StateModel other = (StateModel) obj;
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
