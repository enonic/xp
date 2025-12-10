package com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6;


import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;

public class Pre6VersionDumpEntryJson
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

    @SuppressWarnings("unused")
    public Pre6VersionDumpEntryJson()
    {
    }

    private Pre6VersionDumpEntryJson( final Builder builder )
    {
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        version = builder.version;
        nodeBlobKey = builder.nodeBlobKey;
        indexConfigBlobKey = builder.indexConfigBlobKey;
        accessControlBlobKey = builder.accessControlBlobKey;
        nodeState = builder.nodeState;
    }

    public static VersionMeta fromJson( final Pre6VersionDumpEntryJson json )
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
            .build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Pre6VersionDumpEntryJson source )
    {
        return new Builder( source );
    }

    public String getNodePath()
    {
        return nodePath;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public String getVersion()
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

    public String getNodeState()
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

        private Builder( final Pre6VersionDumpEntryJson source )
        {
            this.nodePath = source.getNodePath();
            this.timestamp = source.getTimestamp();
            this.version = source.getVersion();
            this.nodeBlobKey = source.getNodeBlobKey();
            this.indexConfigBlobKey = source.getIndexConfigBlobKey();
            this.accessControlBlobKey = source.getAccessControlBlobKey();
            this.nodeState = source.getNodeState();
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

        public Pre6VersionDumpEntryJson build()
        {
            return new Pre6VersionDumpEntryJson( this );
        }
    }
}
