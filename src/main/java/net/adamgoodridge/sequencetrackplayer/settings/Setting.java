package net.adamgoodridge.sequencetrackplayer.settings;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.*;

//todo SettingArrays
@Data
@Document("settings")
public class Setting {
    @Id
    private String id;
    @Field(name = "name")
    private String name;
    @Field(name = "value")
    private String value;

    public Setting(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Setting() {

    }

    public Setting(SettingName settingName, String value) {
        this.name = settingName.toString().toLowerCase();
        this.value = value;
    }
	
	public Setting(SettingName settingName, Boolean scanning) {
        this(settingName, String.valueOf(scanning));
	}


    //for debugging
    //todo: remove this
    @Override
    public String toString() {
        return "Setting{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
