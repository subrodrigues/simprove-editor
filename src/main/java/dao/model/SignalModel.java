/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:42
 */

package dao.model;

public class SignalModel {
    private int id;
    private SignalTypeModel type;

    public SignalModel(int id, SignalTypeModel type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SignalTypeModel getType() {
        return type;
    }

    public void setType(SignalTypeModel type) {
        this.type = type;
    }
}
