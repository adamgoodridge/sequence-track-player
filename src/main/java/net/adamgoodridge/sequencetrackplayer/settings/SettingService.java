package net.adamgoodridge.sequencetrackplayer.settings;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
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
                String searchValue = settingName.toString().toLowerCase();
                Optional<Setting> dbValueOptional = settingRepository.findSettingByNameEquals(searchValue);
                if (dbValueOptional.isPresent()) continue;
                if(!checkedOnload) {
                    String errorMessage = String.format("%s isn't in the db, adding it right now with the value of %s", settingName, settingName.getDefaultValue());
                    logger.error(errorMessage);
                }
                settingRepository.save(new Setting(settingName.toString().toLowerCase(), settingName.getDefaultValue().toLowerCase()));
        }
        }
    public void saveSettings(List<Setting> settings) {
        if(settings.size() != SettingName.values().length) {
            throw new InvalidOperationError("You must provide all settings to save them.");
        }
        settingRepository.deleteAll();
        settingRepository.saveAll(settings);
    }
    public Optional<Setting> getSetting(String name) {
        return settingRepository.findSettingByNameEquals(name.toLowerCase());
    }
    public Setting getSetting(SettingName settingName) {
        return getSetting(settingName.name()).orElse(null);
    }

    public void saveBoolean(SettingName name,boolean value) {
        if(!name.isBoolean())
            throw new ShufflePlayerError("The setting name must be a boolean");
        update(name, value);
    }
    public void saveCalendarView(boolean value) {
        update(SettingName.CALENDAR_VIEW, value);
    }

    public boolean isScanning() {
        Setting setting = getSetting(SettingName.IS_SCANNING);
        return Boolean.parseBoolean(setting.getValue());
    }
    public boolean getBoolean(SettingName name) {
        Setting setting = getSetting(name);
        return Boolean.parseBoolean(setting.getValue());
    }
    public int getInteger(SettingName name) {
        Setting setting = getSetting(name);
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
        String searchName = settingName.name().toLowerCase();
        Optional<Setting> optional = settingRepository.findSettingByNameEquals(searchName);
        if (optional.isEmpty()) {
            throw new JsonNotFoundError("Setting not found: " + settingName);
        }
        Setting setting = optional.get();
        setting.setValue(String.valueOf(value));
        settingRepository.save(setting);
    }
    public PreferredRandomSettings getPreferredRandomSettings() {
        String day = getSetting(SettingName.PREFERRED_DAY_OF_WEEK).getValue();
        int time = preferredDateAndTime();
        int trackCurrentCount = getInteger(SettingName.TRACK_CURRENT_COUNT);
        return new PreferredRandomSettings.Builder()
                .day(day)
                .time(time)
                .regularlyTrackChange(getBoolean(SettingName.REGULARLY_CHANGE_TO_RANDOM))
                .trackCurrentCount(trackCurrentCount)
                .build();
    }
    public int preferredDateAndTime() {
        String timeOfDay = getSetting(SettingName.PREFERRED_HOUR_OF_DAY).getValue();
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


