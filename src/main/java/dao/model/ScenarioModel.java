/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:42
 */

package dao.model;

import java.util.List;

public class ScenarioModel {
    private int id;
    private String name;
    private String briefing;
    private List<StateModel> states;
    private List<StateModel> actions;

    public ScenarioModel(int id, String name, String briefing, List<StateModel> states, List<StateModel> ãctions) {
        this.id = id;
        this.name = name;
        this.briefing = briefing;
        this.states = states;
        this.actions = ãctions;
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

    public List<StateModel> getStates() {
        return states;
    }

    public void setStates(List<StateModel> states) {
        this.states = states;
    }

    public List<StateModel> getActions() {
        return actions;
    }

    public void setActions(List<StateModel> actions) {
        this.actions = actions;
    }
}
