/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:41
 */

package dao.model;

import java.io.Serializable;
import java.util.List;

public class TipModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String message;
    private ActorModel actor;
    private int duration;
    private float time; // -1 means that it has no specific time to show
    private List<ActionModel> actionsDone; // Used as logical conditions
    private List<ActionModel> actionsTodo; // Used as logical conditions

    public TipModel(int id) {
        this.id = id;
        this.message = "";
        this.actor = null;
        this.duration = 0;
        this.time = 0;
        this.actionsDone = null;
        this.actionsTodo = null;
    }

    public TipModel(int id, String message, ActorModel actor, int duration, float time, List<ActionModel> actionsDone, List<ActionModel> actionsTodo) {
        this.id = id;
        this.message = message;
        this.actor = actor;
        this.duration = duration;
        this.time = time;
        this.actionsDone = actionsDone;
        this.actionsTodo = actionsTodo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public List<ActionModel> getActionsDone() {
        return actionsDone;
    }

    public void setActionsDone(List<ActionModel> actionsDone) {
        this.actionsDone = actionsDone;
    }

    public List<ActionModel> getActionsTodo() {
        return actionsTodo;
    }

    public void setActionsTodo(List<ActionModel> actionsTodo) {
        this.actionsTodo = actionsTodo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!TipModel.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final TipModel other = (TipModel) obj;
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
        return this.getMessage();
    }
}
