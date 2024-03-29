/*
 * Created by Filipe André Rodrigues on 15-04-2019 16:25
 */

package dao.model;

import java.io.Serializable;
import java.util.List;

// TODO: create composition models to support both the numeric range and list of Strings values
public class SignalTemplateModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int type;
    private String name;
    private String unit;
    private float minRange;
    private float maxRange;

    /**
     * Granularity explanation.
     *
     * # .0f means 0 decimal cases (i.e. integer)
     * # .1f means 1 decimal case
     * # and so on...
     */
    private float granularity;
    private List<String> physicalOptions;
    private List<Integer> plotY;

    public SignalTemplateModel(int id, int type, String name, String unit, float minRange,
                               float maxRange, float granularity, List<String> physicalOptions, List<Integer> plotY) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.unit = unit;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.granularity = granularity;
        this.physicalOptions = physicalOptions;
        this.plotY = plotY;
    }

    public SignalTemplateModel(int id, String name) {
        this.id = id;
        this.name = name;
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

    public List<Integer> getPlotY() {
        return plotY;
    }

    public void setPlotY(List<Integer> plotY) {
        this.plotY = plotY;
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

    public boolean isNumericalSignal(){
        return this.type == 0;
    }

    public boolean isCategoricalSignal(){
        return this.type == 1;
    }

    public boolean isGraphicalSignal(){
        return this.type == 2;
    }
}
