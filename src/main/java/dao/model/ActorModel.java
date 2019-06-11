/*
 * Created by Filipe André Rodrigues on 04-06-2019 19:37
 */

package dao.model;

import java.io.Serializable;

public class ActorModel  implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private TypeModel type;

    public ActorModel(int id, String name, TypeModel type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public ActorModel(String name, TypeModel type) {
        this.id = -1;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeModel getType() {
        return type;
    }

    public void setType(TypeModel type) {
        this.type = type;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!TipModel.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final ActorModel other = (ActorModel) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
