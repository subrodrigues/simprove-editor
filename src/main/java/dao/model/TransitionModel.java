/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:41
 */

package dao.model;

import java.io.Serializable;

public class TransitionModel  implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private float duration;
    private int stateId;

    public TransitionModel(int id, float duration, int stateId) {
        this.id = id;
        this.duration = duration;
        this.stateId = stateId;
    }

    public TransitionModel(float duration, int stateId) {
        this.duration = duration;
        this.stateId = stateId;
    }

    public TransitionModel(int stateId) {
        this.duration = -1;
        this.stateId = stateId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }
}
