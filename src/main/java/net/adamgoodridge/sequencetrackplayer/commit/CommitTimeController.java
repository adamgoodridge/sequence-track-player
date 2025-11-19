package net.adamgoodridge.sequencetrackplayer.commit;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CommitTimeController {

    @GetMapping(value = "/api/commit-time", produces = "application/json")
    public Map<String, String> getCommitTime() {
        return Map.of("commitTime", ConstantTextProperties.getInstance().getCompileTime());
    }
}

