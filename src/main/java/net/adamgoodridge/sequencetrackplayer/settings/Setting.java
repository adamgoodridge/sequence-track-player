package net.adamgoodridge.sequencetrackplayer.settings;

import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.*;

//todo SettingArrays
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
	
	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
