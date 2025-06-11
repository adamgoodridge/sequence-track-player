package net.adamgoodridge.sequencetrackplayer.settings;

import org.springframework.data.mongodb.core.mapping.*;

import javax.persistence.*;
import java.util.*;

@Document("setting_arrays")
public class SettingArray {
    @Id
    private String id;

    @Field(name = "setting_name")
    private String settingName;

    @Field(name = "values")
    private List<String> values;

    public SettingArray(String settingName, List<String> values) {
        this.settingName = settingName;
        this.values = values;
    }

    // Getters and setters

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}