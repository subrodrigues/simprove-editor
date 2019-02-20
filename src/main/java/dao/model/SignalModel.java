package dao.model;

public class SignalModel {
    private int id;
    private SignalTypeModel type;
    private String name;
    private float value;

    public SignalModel(int id, SignalTypeModel type, String name, float value) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.value = value;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
