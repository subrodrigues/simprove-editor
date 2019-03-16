/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:41
 */

package dao.model;

public class TypeModel {
    private int id;
    private int typeId;
    private String name;

    public TypeModel(int id, int typeId, String name) {
        this.id = id;
        this.typeId = typeId;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
