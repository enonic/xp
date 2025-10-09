package com.enonic.xp.repo.impl.dump.serializer.json;


import java.time.Instant;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;

public class VersionDumpEntryJson
{
    @JsonProperty("nodePath")
    private String nodePath;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("version")
    private String version;

    @JsonProperty("nodeBlobKey")
    private String nodeBlobKey;

    @JsonProperty("indexConfigBlobKey")
    private String indexConfigBlobKey;

    @JsonProperty("accessControlBlobKey")
    private String accessControlBlobKey;

    @JsonProperty("nodeState")
    private String nodeState;

    @JsonProperty("commitId")
    private String commitId;

    public VersionDumpEntryJson()
    {
    }

    private VersionDumpEntryJson( final Builder builder )
    {
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        version = builder.version;
        nodeBlobKey = builder.nodeBlobKey;
        indexConfigBlobKey = builder.indexConfigBlobKey;
        accessControlBlobKey = builder.accessControlBlobKey;
        commitId = builder.commitId;
    }

    public static VersionMeta fromJson( final VersionDumpEntryJson json )
    {
        return VersionMeta.create()
            .nodePath( new NodePath( json.nodePath ) )
            .timestamp( json.getTimestamp() != null ? Instant.parse( json.getTimestamp() ) : null )
            .version( json.getVersion() != null ? NodeVersionId.from( json.getVersion() ) : null )
            .nodeVersionKey( NodeVersionKey.create()
                                 .nodeBlobKey( BlobKey.from( json.getNodeBlobKey() ) )
                                 .indexConfigBlobKey( BlobKey.from( json.getIndexConfigBlobKey() ) )
                                 .accessControlBlobKey( BlobKey.from( json.getAccessControlBlobKey() ) )
                                 .build() )
            .nodeCommitId( json.getCommitId() == null ? null : NodeCommitId.from( json.getCommitId() ) )
            .build();
    }

    public static VersionDumpEntryJson from( final VersionMeta meta )
    {
        return VersionDumpEntryJson.create().
            nodePath( meta.getNodePath().toString() ).
            timestamp( Objects.toString( meta.getTimestamp(), null ) ).
            version( Objects.toString( meta.getVersion(), null ) ).
            nodeBlobKey( meta.getNodeVersionKey().getNodeBlobKey().toString() ).
            indexConfigBlobKey( meta.getNodeVersionKey().getIndexConfigBlobKey().toString() ).
            accessControlBlobKey( meta.getNodeVersionKey().getAccessControlBlobKey().toString() ).
            commitId( Objects.toString( meta.getNodeCommitId(), null ) ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final VersionDumpEntryJson source )
    {
        return new Builder( source );
    }

    public String getNodePath()
    {
        return nodePath;
    }

    private String getTimestamp()
    {
        return timestamp;
    }

    private String getVersion()
    {
        return version;
    }

    public String getNodeBlobKey()
    {
        return nodeBlobKey;
    }

    public String getIndexConfigBlobKey()
    {
        return indexConfigBlobKey;
    }

    public String getAccessControlBlobKey()
    {
        return accessControlBlobKey;
    }

    public String getCommitId()
    {
        return commitId;
    }

    public static final class Builder
    {
        private String nodePath;

        private String timestamp;

        private String version;

        private String nodeBlobKey;

        private String indexConfigBlobKey;

        private String accessControlBlobKey;

        private String nodeState;

        private String commitId;

        private Builder()
        {
        }

        private Builder( final VersionDumpEntryJson source )
        {
            this.nodePath = source.getNodePath();
            this.timestamp = source.getTimestamp();
            this.version = source.getVersion();
            this.nodeBlobKey = source.getNodeBlobKey();
            this.indexConfigBlobKey = source.getIndexConfigBlobKey();
            this.accessControlBlobKey = source.getAccessControlBlobKey();
            this.commitId = source.getCommitId();
        }

        public Builder nodePath( final String val )
        {
            nodePath = val;
            return this;
        }

        public Builder timestamp( final String val )
        {
            timestamp = val;
            return this;
        }

        public Builder version( final String val )
        {
            version = val;
            return this;
        }

        public Builder nodeBlobKey( final String val )
        {
            nodeBlobKey = val;
            return this;
        }

        public Builder indexConfigBlobKey( final String val )
        {
            indexConfigBlobKey = val;
            return this;
        }

        public Builder accessControlBlobKey( final String val )
        {
            accessControlBlobKey = val;
            return this;
        }

        public Builder commitId( final String val )
        {
            commitId = val;
            return this;
        }

        public VersionDumpEntryJson build()
        {
            return new VersionDumpEntryJson( this );
        }
    }
}
