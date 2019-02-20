/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:42
 */

package dao.model;

public class SignalTypeModel {
    private int id;
    private int type;

    public SignalTypeModel(int id, int type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
