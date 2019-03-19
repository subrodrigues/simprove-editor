/*
 * Created by Filipe André Rodrigues on 14-03-2019 17:45
 */

package events;

import base.BaseEvent;
import base.ResponseError;
import dao.model.TypeModel;

import java.util.List;

public class ActionTypesEvent extends BaseEvent {
    private List<TypeModel> actionTypes;

    public ActionTypesEvent(List<TypeModel> actionTypes) {
        this.actionTypes = actionTypes;
    }

    public ActionTypesEvent(ResponseError error) {
        super(error);
    }

    public List<TypeModel> getActionTypes() {
        return actionTypes;
    }

    public void setActionTypes(List<TypeModel> actionTypes) {
        this.actionTypes = actionTypes;
    }
}
