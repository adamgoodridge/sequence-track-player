package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy;

import org.springframework.stereotype.*;

import java.util.*;

@Service
public class RandomNumberGenerator {

	private static final Random rnd = new Random();

	public int getRandomNumber(int bound) {
		return rnd.nextInt(bound);
	}
}
