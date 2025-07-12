package net.adamgoodridge.sequencetrackplayer.feeder;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
public class FeedRequest {
    private final String name;
    private String path;
    private Long feedId;
    private final FeedRequestType feedRequestType;


    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
    public boolean hasId() {
        return feedId != null && feedId > 0;
    }
    public boolean isRequestType(FeedRequestType feedRequestType) {
        return this.feedRequestType == feedRequestType;
    }

    public String getFullSystemPath() {
        return ConstantTextFileSystem.getInstance().getSharePath() + pathWithoutStartingSlash();
    }
    private String pathWithoutStartingSlash() {
        return path.startsWith(ConstantTextFileSystem.getInstance().getSlash()) ? path.substring(1) : path;
    }
    public void setFeedId(Long feedId) {
        if(hasId())
            throw new ServerError("Feed id already set");
        this.feedId = feedId;
    }

    public void removeStartingSlash() {
        this.path = pathWithoutStartingSlash();
    }
    public Long getFeedId() {
        return feedId;
    }


    public static Builder builder() {
        return new Builder();
    }
    public FeedRequest(Builder builder) {
        this.name = builder.name;
        this.path = builder.path;
        this.feedId = builder.feedId;
        this.feedRequestType = builder.feedRequestType;
    }
    public static class Builder {
        private String name;
        private String path;
        private Long feedId;
        private FeedRequestType feedRequestType;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder feedId(Long feedId) {
            this.feedId = feedId;
            return this;
        }

        public Builder feedRequestType(FeedRequestType feedRequestType) {
            this.feedRequestType = feedRequestType;
            return this;
        }


        public FeedRequest build() {
            return new FeedRequest(this);
        }
    }

    }