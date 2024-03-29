package dao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SignalModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int type;
    private String name;
    private String value;
    private List<Integer> plotYValue;

    private SignalTemplateModel template;

    public SignalModel(int id, int type, String name, String value, List<Integer> plotYValue) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.value = value;
        this.plotYValue = plotYValue;

        this.template = null;
    }

    public SignalModel(int id){
        this.id = id;
        this.type = -1;
        this.name = "";
        this.value = "";
        this.plotYValue = new ArrayList<>();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Integer> getPlotYValue() {
        return plotYValue;
    }

    public void setPlotYValue(List<Integer> plotYValue) {
        this.plotYValue = plotYValue;
    }

    public SignalTemplateModel getTemplate() {
        return template;
    }

    public void setTemplate(SignalTemplateModel template) {
        this.template = template;
    }

    public boolean isGraphicalSignal(){
        return type == 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!SignalModel.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final SignalModel other = (SignalModel) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.id;
        return hash;
    }

}
