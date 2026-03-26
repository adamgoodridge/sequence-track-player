package net.adamgoodridge.sequencetrackplayer.utils;

import org.springframework.stereotype.*;

import java.util.*;

@Service
public class RandomNumberGenerator {

	private static final Random rnd = new Random();

	public int getRandomNumber(int bound) {
        if (bound <= 0)
			return 0;
        return rnd.nextInt(bound);
}
}
