/*
 * Created by Filipe André Rodrigues on 15-04-2019 16:25
 */

package dao.model;

import java.util.List;

// TODO: create composition models to support both the numeric range and list of Strings values
public class SignalTemplateModel {
    private int id;
    private int type;
    private String name;
    private String unit;
    private float minRange;
    private float maxRange;
    private float granularity;
    private List<String> physicalOptions;

    public SignalTemplateModel(int id, int type, String name, String unit, float minRange, float maxRange, float granularity, List<String> physicalOptions) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.unit = unit;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.granularity = granularity;
        this.physicalOptions = physicalOptions;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getMinRange() {
        return minRange;
    }

    public void setMinRange(float minRange) {
        this.minRange = minRange;
    }

    public float getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(float maxRange) {
        this.maxRange = maxRange;
    }

    public float getGranularity() {
        return granularity;
    }

    public void setGranularity(float granularity) {
        this.granularity = granularity;
    }

    public List<String> getPhysicalOptions() {
        return physicalOptions;
    }

    public void setPhysicalOptions(List<String> physicalOptions) {
        this.physicalOptions = physicalOptions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!StateModel.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final SignalTemplateModel other = (SignalTemplateModel) obj;
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
