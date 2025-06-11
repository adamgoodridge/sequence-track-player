package net.adamgoodridge.sequencetrackplayer.setting;

public class SettingServiceTests {
}
//todo


/*
@Test
void getArrayValues_ReturnsCorrectValues() {
    // Given
    String settingName = "allowed_extensions";
    List<String> expectedValues = Arrays.asList("mp3", "wav", "ogg");
    SettingArray mockSettingArray = new SettingArray(settingName, expectedValues);
    when(settingArrayRepository.findBySettingName(settingName))
            .thenReturn(Optional.of(mockSettingArray));

    // When
    List<String> result = settingService.getArrayValues(SettingName.ALLOWED_EXTENSIONS);

    // Then
    assertEquals(expectedValues, result);
    verify(settingArrayRepository).findBySettingName(settingName);
}*/