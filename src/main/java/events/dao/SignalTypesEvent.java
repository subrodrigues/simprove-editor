/*
 * Created by Filipe André Rodrigues on 28-05-2019 23:47
 */

/*
 * Created by Filipe André Rodrigues on 15-04-2019 18:29
 */

package events.dao;

import base.BaseEvent;
import base.ResponseError;
import dao.model.SignalTemplateModel;

import java.util.List;

public class SignalTypesEvent extends BaseEvent {
    private List<SignalTemplateModel> signalTypes;

    public SignalTypesEvent(List<SignalTemplateModel> signalTypes) {
        this.signalTypes = signalTypes;
    }

    public SignalTypesEvent(ResponseError error) {
        super(error);
    }

    public List<SignalTemplateModel> getSignalTypes() {
        return signalTypes;
    }

    public void setSignalTypes(List<SignalTemplateModel> signalTypes) {
        this.signalTypes = signalTypes;
    }
}
