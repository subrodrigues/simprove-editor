/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:41
 */

package dao.model;

public class TransitionModel {
    private int id;
    private int duration;
    private int stateId;

    public TransitionModel(int id, int duration, int stateId) {
        this.id = id;
        this.duration = duration;
        this.stateId = stateId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }
}
