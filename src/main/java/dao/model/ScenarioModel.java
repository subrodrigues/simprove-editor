/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:42
 */

package dao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScenarioModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String briefing;
    private int score;
    private int minScore;
    private String defaultErrorMessage;
    private List<StateModel> states;
    private List<ActionModel> actions;

    public ScenarioModel() {
        this.id = 0;
        this.name = "";
        this.briefing = "";
        this.score = 100;
        this.minScore = 0;
        this.states = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    public ScenarioModel(int id, String name, String briefing, int score, int minScore, String defaultErrorMessage, List<StateModel> states, List<ActionModel> actions) {
        this.id = id;
        this.name = name;
        this.briefing = briefing;
        this.score = score;
        this.minScore = minScore;
        this.defaultErrorMessage = defaultErrorMessage;
        this.states = states;
        this.actions = actions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBriefing() {
        return briefing;
    }

    public void setBriefing(String briefing) {
        this.briefing = briefing;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(int minScore) {
        this.minScore = minScore;
    }

    public List<StateModel> getStates() {
        return states;
    }

    public void setStates(List<StateModel> states) {
        this.states = states;
    }

    public List<ActionModel> getActions() {
        return actions;
    }

    public void setActions(List<ActionModel> actions) {
        this.actions = actions;
    }

    public String getDefaultErrorMessage() {
        return defaultErrorMessage;
    }

    public void setDefaultErrorMessage(String defaultErrorMessage) {
        this.defaultErrorMessage = defaultErrorMessage;
    }
}
