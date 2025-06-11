package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.feeder.AudioFeeder;
import net.adamgoodridge.sequencetrackplayer.feeder.repository.AudioFeederRepository;
import net.adamgoodridge.sequencetrackplayer.mock.respository.AudioFeederRepositoryMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AudioFeederRepositoryTest extends AbstractSpringBootTest {
    @Autowired
    private AudioFeederRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        new AudioFeederRepositoryMock().fillWithMockData(repository);
    }


    @Test
    void testFindAll() {
        List<AudioFeeder> audioFeederList = repository.findAll();
        assertNotNull(audioFeederList);
        assertFalse(audioFeederList.isEmpty());
    }

    @Test
    void testGetRandomAudioFileNotNull() {
        AudioFeeder audioFeeder = repository.getRandomAudioFileNotNull();
        assertNotNull(audioFeeder);
    }


    @Test
    void testGetAudioFeederByFeedName() {
        AudioFeeder audioFeeder = repository.getAudioFeederByFeedName("testDemo");
        assertNotNull(audioFeeder);
    }
}