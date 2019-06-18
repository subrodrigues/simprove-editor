/*
 * Created by Filipe André Rodrigues on 18-06-2019 19:54
 */

package dao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScoreModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int scoreLost;
    private float lossOvertime;
    private List<StateModel> startStates;
    private List<StateModel> endStates;

    public ScoreModel(int id, int scoreLost, float lossOvertime, List<StateModel> startStates, List<StateModel> endStates) {
        this.id = id;
        this.scoreLost = scoreLost;
        this.lossOvertime = lossOvertime;
        this.startStates = startStates;
        this.endStates = endStates;
    }

    public ScoreModel(int id) {
        this.id = id;

        this.startStates = new ArrayList<>();
        this.endStates = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScoreLost() {
        return scoreLost;
    }

    public void setScoreLost(int scoreLost) {
        this.scoreLost = scoreLost;
    }

    public float getLossOvertime() {
        return lossOvertime;
    }

    public void setLossOvertime(float lossOvertime) {
        this.lossOvertime = lossOvertime;
    }

    public List<StateModel> getStartStates() {
        return startStates;
    }

    public void setStartStates(List<StateModel> startStates) {
        this.startStates = startStates;
    }

    public List<StateModel> getEndStates() {
        return endStates;
    }

    public void setEndStates(List<StateModel> endStates) {
        this.endStates = endStates;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!ScoreModel.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final ScoreModel other = (ScoreModel) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.id;
        return hash;
    }

}
