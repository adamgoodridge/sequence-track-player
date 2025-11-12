package net.adamgoodridge.sequencetrackplayer.settings;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.*;

import javax.persistence.*;
import java.util.*;

@Data
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


}