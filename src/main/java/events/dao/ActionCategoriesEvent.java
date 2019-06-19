/*
 * Created by Filipe André Rodrigues on 19-06-2019 14:27
 */

package events.dao;

import base.BaseEvent;
import base.ResponseError;
import dao.model.TypeModel;

import java.util.List;

public class ActionCategoriesEvent extends BaseEvent {
    private List<TypeModel> actionCategories;

    public ActionCategoriesEvent(List<TypeModel> actionCategories) {
        this.actionCategories = actionCategories;
    }

    public ActionCategoriesEvent(ResponseError error) {
        super(error);
    }

    public List<TypeModel> getActionCategories() {
        return actionCategories;
    }

    public void setActionCategories(List<TypeModel> actionCategories) {
        this.actionCategories = actionCategories;
    }
}
