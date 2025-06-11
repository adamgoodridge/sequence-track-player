package net.adamgoodridge.sequencetrackplayer.settings;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class SettingService {

    private final SettingRepository settingRepository;
    private static final Logger logger = LoggerFactory.getLogger(SettingService.class.getName());
    private final SettingArrayRepository settingArrayRepository;


    @Autowired
    public SettingService(SettingRepository settingRepository, SettingArrayRepository settingArrayRepository) {
        this.settingRepository = settingRepository;
        this.settingArrayRepository = settingArrayRepository;
        //making sure values are there in the db
        defaults(true);
        }
        //checkedOnload MEANING ALL setting should there, just making sure
    private void defaults(boolean checkedOnload) {
        for(SettingName settingName: SettingName.values()) {
                Setting dbValue = getSetting(settingName.toString().toLowerCase());
                if (dbValue != null) continue;
                if(!checkedOnload) {
                    String errorMessage = String.format("%s isn't in the db, adding it right now with the value of %s", settingName, settingName.getDefaultValue());
                    logger.error(errorMessage);
                }
                settingRepository.save(new Setting(settingName.toString().toLowerCase(), settingName.getDefaultValue().toLowerCase()));
        }
        }
    public void saveSettings(List<Setting> settings) {
        settingRepository.deleteAll();
        settingRepository.saveAll(settings);
    }
    public Setting getSetting(String name) {
        return settingRepository.findSettingByNameEquals(name);
    }
    public Setting getSetting(SettingName settingName) {
        String name = settingName.toString().toLowerCase();
        return settingRepository.findSettingByNameEquals(name);
    }
    public String getSettingValue(String settingName){
        return settingRepository.findSettingByNameEquals(settingName).getValue();
    }
    public String getSettingValue(SettingName settingName) {
        return settingRepository.findSettingByNameEquals(settingName.toString().toLowerCase()).getValue();
    }

    public void saveBoolean(SettingName name,boolean value) {
        if(!name.isBoolean())  //TODO: this is a bit of a hack, but it works)
            throw new Error("The setting name must be a boolean");
        update(name, value);
    }
    public void saveCalendarView(boolean value) {
        update(SettingName.CALENDAR_VIEW, value);
    }

    public boolean isScanning() {
        Setting setting = settingRepository.findSettingByNameEquals(SettingName.IS_SCANNING.toString().toLowerCase());
        return Boolean.parseBoolean(setting.getValue());
    }
    public boolean getBoolean(SettingName name) {
        Setting setting = settingRepository.findSettingByNameEquals(name.toString().toLowerCase());
        return Boolean.parseBoolean(setting.getValue());
    }
    public int getInteger(SettingName name) {
        Setting setting = settingRepository.findSettingByNameEquals(name.toString().toLowerCase());
        return Integer.parseInt(setting.getValue());
    }
    public void deleteAndReset() {
        settingRepository.deleteAll();
        defaults(false);
    }
    public List<Setting> getSettings() {
        return settingRepository.findAll();
    }

    private void update(SettingName settingName, boolean value) {
        Setting setting = settingRepository.findSettingByNameEquals(settingName.toString().toLowerCase());
        setting.setValue(String.valueOf(value));
        settingRepository.save(setting);
    }
        public PreferredRandomSettings getPreferredRandomSetting() {
        return null;
    }

    public String preferredDayOfWeek() {
        String dayOfWeek = getSettingValue(SettingName.DAY_OF_WEEK);
        if (dayOfWeek.equals(ConstantText.SETTINGS_VALUE_DAY_OF_WEEK_THIS_DAY))
            dayOfWeek = TimeUtils.getDay(java.time.LocalDate.now().toString());
        return dayOfWeek;
    }

    public PreferredRandomSettings getPreferredRandomSettings() {
        String day = getSettingValue(SettingName.DAY_OF_WEEK);
        int time = preferredDateAndTime();
        return new PreferredRandomSettings.Builder()
                .day(day)
                .time(time)
                .trackCurrentCount(getInteger(SettingName.TRACK_CURRENT_COUNT))
                .build();
    }
    public int preferredDateAndTime() {
        String timeOfDay = getSettingValue(SettingName.HOUR_OF_DAY);
        if (timeOfDay.equals(ConstantText.SETTINGS_VALUE_TIME_OF_DAY_THIS_HOUR))
            return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (timeOfDay.equals("*"))
            return -1;
        return Integer.parseInt(timeOfDay);
    }
    public List<String> getArrayValues(SettingName settingName) {
        return settingArrayRepository
                .findBySettingName(settingName.toString().toLowerCase())
                .map(SettingArray::getValues)
                .orElse(Collections.emptyList());
    }

    public void saveArrayValues(SettingName settingName, List<String> values) {
        SettingArray settingArray = settingArrayRepository
                .findBySettingName(settingName.toString().toLowerCase())
                .orElse(new SettingArray(settingName.toString().toLowerCase(), new ArrayList<>()));

        settingArray.setValues(values);
        settingArrayRepository.save(settingArray);
    }
}


