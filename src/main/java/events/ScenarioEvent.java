/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:26
 */

package events;

import base.BaseEvent;
import base.ResponseError;
import dao.model.ScenarioModel;

public class ScenarioEvent extends BaseEvent {
    private ScenarioModel scenario;

    public ScenarioEvent(ScenarioModel scenario) {
        this.scenario = scenario;
    }

    public ScenarioEvent(ResponseError error) {
        super(error);
    }

    public ScenarioModel getScenario() {
        return scenario;
    }

    public void setScenario(ScenarioModel scenario) {
        this.scenario = scenario;
    }
}
