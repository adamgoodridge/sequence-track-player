package net.adamgoodridge.sequencetrackplayer.statistic;

import net.adamgoodridge.sequencetrackplayer.settings.*;
import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Repository
public interface StatisticRepository  extends MongoRepository<Statistic, Long> {
	public Statistic findByDate(Date date);
	@Query("{ 'date' : { '$gte' : ?0, '$lte' : ?1 } }")
	List<Statistic> findByDateBetween(Date from, Date to);
}
