/*
 * Created by Filipe André Rodrigues on 20-02-2019 19:41
 */

package dao.model;

import java.io.Serializable;

public class TypeModel implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!TypeModel.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final TypeModel other = (TypeModel) obj;
        return this.typeId == other.typeId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.typeId;
        return hash;
    }

}
