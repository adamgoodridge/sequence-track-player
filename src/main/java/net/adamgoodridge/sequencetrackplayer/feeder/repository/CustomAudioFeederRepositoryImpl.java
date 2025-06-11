package net.adamgoodridge.sequencetrackplayer.feeder.repository;

import net.adamgoodridge.sequencetrackplayer.feeder.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.*;

import java.util.*;

@SuppressWarnings("unused")
@Repository
public class CustomAudioFeederRepositoryImpl implements CustomAudioFeederRepository {
    private final MongoOperations mongoOperations;
    private static final String COLLECTION_NAME = "currentAudioFeeder";


    @Autowired
    public CustomAudioFeederRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    //for shuffle
    //https://www.baeldung.com/spring-data-mongodb-projections-aggregations
    public AudioFeeder getRandomBySessionId(String sessionId) {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("sessionId").is(sessionId));
        SampleOperation sampleOperation = Aggregation.sample(1);
        Aggregation aggregation = Aggregation.newAggregation(matchOperation,sampleOperation);
        AggregationResults<AudioFeeder> audioFeederAggregate = mongoOperations.aggregate(aggregation, COLLECTION_NAME, AudioFeeder.class);
        return audioFeederAggregate.getUniqueMappedResult();
    }
    public AudioFeeder getRandomAudioFileNotNull() {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("audioInfo").exists(true));
        SampleOperation sampleOperation = Aggregation.sample(1);
        Aggregation aggregation = Aggregation.newAggregation(matchOperation,sampleOperation);
        AggregationResults<AudioFeeder> audioFeederAggregate = mongoOperations.aggregate(aggregation, COLLECTION_NAME, AudioFeeder.class);
        return audioFeederAggregate.getUniqueMappedResult();
    }
    public List<AudioFeeder> getAllAudioNegativeOne() {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("audioInfo.fileNo").is(-1));
        Aggregation aggregation = Aggregation.newAggregation(matchOperation);
        AggregationResults<AudioFeeder> audioFeederAggregate = mongoOperations.aggregate(aggregation, COLLECTION_NAME, AudioFeeder.class);
        return audioFeederAggregate.getMappedResults();
    }
    @Override
    public List<AudioFeeder> getAllByIsIncludeInFullScreenShuffle() {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("isIncludeInFullScreenShuffle").is(true));
        Aggregation aggregation = Aggregation.newAggregation(matchOperation);
        AggregationResults<AudioFeeder> audioFeederAggregate = mongoOperations.aggregate(aggregation, COLLECTION_NAME, AudioFeeder.class);
        return audioFeederAggregate.getMappedResults();

    }

    @Override
    public AudioFeeder getAudioFeederByFeedName(String feedName) {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("feedName").is(feedName).andOperator(Criteria.where("audioInfo").exists(true)));
        SampleOperation sampleOperation = Aggregation.sample(1);
        Aggregation aggregation = Aggregation.newAggregation(matchOperation,sampleOperation);
        AggregationResults<AudioFeeder> audioFeederAggregate = mongoOperations.aggregate(aggregation, COLLECTION_NAME, AudioFeeder.class);
        return audioFeederAggregate.getUniqueMappedResult();
    }


}
