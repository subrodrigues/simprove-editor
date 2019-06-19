/*
 * Created by Filipe André Rodrigues on 19-06-2019 15:58
 */

package utils;

import dao.model.TypeModel;

import java.util.List;

public class LogicUtils {

    public static TypeModel getActionTypeByName(String name, List<TypeModel> items) {
        for(TypeModel type: items){
            if(type.getName().equals(name)){
                return type;
            }
        }

        return new TypeModel(-1, -1, name); // When we have no correspondency => Create a "new" type
    }
}
