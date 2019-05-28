/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:42
 */

package dao.model;

import java.io.Serializable;

public class SignalTypeModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int typeId;

    public SignalTypeModel(int id, int typeId) {
        this.id = id;
        this.typeId = typeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }


}
