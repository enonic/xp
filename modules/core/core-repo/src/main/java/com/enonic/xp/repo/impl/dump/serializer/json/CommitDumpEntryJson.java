package com.enonic.xp.repo.impl.dump.serializer.json;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.security.PrincipalKey;

@JsonPropertyOrder(value = {"commitId", "timestamp", "committer", "message"})
public class CommitDumpEntryJson
{
    @JsonProperty("commitId")
    private String commitId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("committer")
    private String committer;

    @JsonProperty("timestamp")
    private String timestamp;

    @SuppressWarnings("unused")
    public CommitDumpEntryJson()
    {
    }

    private CommitDumpEntryJson( final Builder builder )
    {
        commitId = builder.commitId;
        message = builder.message;
        committer = builder.committer;
        timestamp = builder.timestamp;
    }

    public static CommitDumpEntryJson from( final CommitDumpEntry commitDumpEntry )
    {
        return create().
            commitId( commitDumpEntry.nodeCommitId().toString() ).
            message( commitDumpEntry.message() ).
            committer( commitDumpEntry.committer().toString() ).
            timestamp( commitDumpEntry.timestamp().toString() ).
            build();
    }

    public static CommitDumpEntry fromJson( final CommitDumpEntryJson commitDumpEntryJson )
    {
        return new CommitDumpEntry( NodeCommitId.from( commitDumpEntryJson.commitId ), commitDumpEntryJson.message,
                                   Instant.parse( commitDumpEntryJson.timestamp ),
                                   PrincipalKey.from( commitDumpEntryJson.committer ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String commitId;

        private String message;

        private String committer;

        private String timestamp;

        private Builder()
        {
        }

        public Builder commitId( final String commitId )
        {
            this.commitId = commitId;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public Builder committer( final String committer )
        {
            this.committer = committer;
            return this;
        }

        public Builder timestamp( final String timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public CommitDumpEntryJson build()
        {
            return new CommitDumpEntryJson( this );
        }
    }
}
