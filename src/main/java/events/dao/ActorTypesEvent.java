/*
 * Created by Filipe André Rodrigues on 04-06-2019 20:49
 */

package events.dao;

import base.BaseEvent;
import base.ResponseError;
import dao.model.TypeModel;

import java.util.List;

public class ActorTypesEvent extends BaseEvent {
    private List<TypeModel> actorTypes;

    public ActorTypesEvent(List<TypeModel> actorTypes) {
        this.actorTypes = actorTypes;
    }

    public ActorTypesEvent(ResponseError error) {
        super(error);
    }

    public List<TypeModel> getActorTypes() {
        return actorTypes;
    }

    public void setActorTypes(List<TypeModel> actorTypes) {
        this.actorTypes = actorTypes;
    }
}
