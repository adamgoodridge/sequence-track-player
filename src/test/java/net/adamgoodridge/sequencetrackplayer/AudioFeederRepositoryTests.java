package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.feeder.AudioFeeder;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.AudioFeederRepository;
import net.adamgoodridge.sequencetrackplayer.mock.respository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AudioFeederRepositoryTests {
    @Autowired
    private AudioFeederRepository audioFeederRepository;

    @BeforeEach
    void setUp() throws IOException {
        new AudioFeederRepositoryMock().fillWithMockData(audioFeederRepository);
    }


    @Test
    void getRandom(){
        AudioFeeder audioFeeder = audioFeederRepository.getRandomAudioFileNotNull();
        assertNotNull(audioFeeder);
    }

    @Test
    void getRandomBySessionId(){
        String sessionId = audioFeederRepository.getRandomAudioFileNotNull().getSessionId();
        AudioFeeder audioFeeder = audioFeederRepository.getRandomBySessionId(sessionId);
        notNull(audioFeeder);
    }
}
