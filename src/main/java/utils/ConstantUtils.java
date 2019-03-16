/*
 * Created by Filipe André Rodrigues on 13-03-2019 22:40
 */

package utils;

import dao.model.TypeModel;

import java.util.ArrayList;
import java.util.List;

public class ConstantUtils {

    /**
     * Return the default Action Categories.
     *
     */
    public static List<TypeModel> requestActionCategories(){
        List<TypeModel> mockedActionCategories = new ArrayList<>();

        mockedActionCategories.add(new TypeModel(-1, 1, "Procedure"));
        mockedActionCategories.add(new TypeModel(-1, 2, "Diagnosis"));
        mockedActionCategories.add(new TypeModel(-1, 3, "Environment"));

        return mockedActionCategories;
    }

}
