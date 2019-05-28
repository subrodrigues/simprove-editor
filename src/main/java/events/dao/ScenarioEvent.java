/*
 * Created by Filipe André Rodrigues on 28-05-2019 23:47
 */

/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:26
 */

package events.dao;

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
