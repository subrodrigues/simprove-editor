/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:42
 */

package dao.model;

import java.util.List;

public class StateModel {
    private int id;
    private TypeModel type;
    private List<SignalModel> signals;
    private TransitionModel transition;
    private List<TipModel> tips;

    public StateModel(int id, TypeModel type, List<SignalModel> signals, TransitionModel transition, List<TipModel> tips) {
        this.id = id;
        this.type = type;
        this.signals = signals;
        this.transition = transition;
        this.tips = tips;
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
}
