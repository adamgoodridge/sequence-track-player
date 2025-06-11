package net.adamgoodridge.sequencetrackplayer.feeder.sequence;

import org.springframework.beans.factory.annotation.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.*;

import java.util.*;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;


//https://www.javaprogramto.com/2019/05/spring-boot-mongodb-auto-generated-field.html
@Service
public class SequenceGeneratorService {
    private final MongoOperations mongoOperations;

    @Autowired
    public SequenceGeneratorService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public long generateSequence(String seqName) {
        AudioFeederSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq", 1), options().returnNew(true).upsert(true), AudioFeederSequence.class);

        return Objects.isNull(counter) ? 1 : counter.getSeq();
    }
    public void resetSequence(String seqName) {
        mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().set("seq", 1), options().returnNew(true).upsert(true), AudioFeederSequence.class);
    }
}
