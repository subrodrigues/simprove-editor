/*
 * Created by Filipe André Rodrigues on 13-08-2019 20:10
 */

package events.dao;

import base.BaseEvent;
import base.ResponseError;
import dao.model.TypeModel;

import java.util.List;

public class ScenarioSettingTypesEvent extends BaseEvent {
    private List<TypeModel> settingTypes;

    public ScenarioSettingTypesEvent(List<TypeModel> settingTypes) {
        this.settingTypes = settingTypes;
    }

    public ScenarioSettingTypesEvent(ResponseError error) {
        super(error);
    }

    public List<TypeModel> getScenarioSettingTypes() {
        return settingTypes;
    }

    public void setScenarioSettingTypes(List<TypeModel> settingTypes) {
        this.settingTypes = settingTypes;
    }
}
