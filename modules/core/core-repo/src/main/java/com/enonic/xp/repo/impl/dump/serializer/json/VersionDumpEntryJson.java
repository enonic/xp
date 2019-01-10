package com.enonic.xp.repo.impl.dump.serializer.json;


import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
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
        nodeState = builder.nodeState;
    }

    public static VersionMeta fromJson( final VersionDumpEntryJson json )
    {
        final NodeVersionKey nodeVersionKey =
            NodeVersionKey.from( json.getNodeBlobKey(), json.getIndexConfigBlobKey(), json.getAccessControlBlobKey() );
        return VersionMeta.create().
            nodePath( NodePath.create( json.nodePath ).build() ).
            timestamp( json.getTimestamp() != null ? Instant.parse( json.getTimestamp() ) : null ).
            version( json.getVersion() != null ? NodeVersionId.from( json.getVersion() ) : null ).
            nodeVersionKey( nodeVersionKey ).
            nodeState( NodeState.from( json.getNodeState() ) ).
            build();
    }

    public static VersionDumpEntryJson from( final VersionMeta meta )
    {
        return VersionDumpEntryJson.create().
            nodePath( meta.getNodePath().toString() ).
            timestamp( meta.getTimestamp() != null ? meta.getTimestamp().toString() : null ).
            version( meta.getVersion() != null ? meta.getVersion().toString() : null ).
            nodeBlobKey( meta.getNodeVersionKey().getNodeBlobKey().toString() ).
            indexConfigBlobKey( meta.getNodeVersionKey().getIndexConfigBlobKey().toString() ).
            accessControlBlobKey( meta.getNodeVersionKey().getAccessControlBlobKey().toString() ).
            build();
    }

    private static Builder create()
    {
        return new Builder();
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

    private String getNodeState()
    {
        return nodeState;
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

        private Builder()
        {
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

        public Builder nodeState( final String val )
        {
            nodeState = val;
            return this;
        }

        public VersionDumpEntryJson build()
        {
            return new VersionDumpEntryJson( this );
        }
    }
}
