package net.adamgoodridge.sequencetrackplayer;


import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.scheduling.annotation.*;

@EnableAsync
@SpringBootApplication
public class SequenceTrackPlayerApplication {

	static void main(String[] args) {
		SpringApplication.run(SequenceTrackPlayerApplication.class, args);
	}

}
