/*
 * Created by Filipe André Rodrigues on 19-06-2019 14:28
 */

package events.dao;

import base.BaseEvent;
import base.ResponseError;
import dao.model.TypeModel;

import java.util.List;

public class ActionSubCategoriesEvent extends BaseEvent {
    private List<TypeModel> actionSubCategories;

    public ActionSubCategoriesEvent(List<TypeModel> actionSubCategories) {
        this.actionSubCategories = actionSubCategories;
    }

    public ActionSubCategoriesEvent(ResponseError error) {
        super(error);
    }

    public List<TypeModel> getActionSubCategories() {
        return actionSubCategories;
    }

    public void setActionSubCategories(List<TypeModel> actionSubCategories) {
        this.actionSubCategories = actionSubCategories;
    }
}
