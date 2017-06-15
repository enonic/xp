package com.enonic.xp.repo.impl.dump.serializer.json;


import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.model.Meta;

public class MetaJson
{
    @JsonProperty("nodePath")
    private String nodePath;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("version")
    private String version;

    @JsonProperty("nodeState")
    private String nodeState;

    @JsonProperty("current")
    private boolean current;

    public MetaJson()
    {
    }

    private MetaJson( final Builder builder )
    {
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        version = builder.version;
        nodeState = builder.nodeState;
        current = builder.current;
    }

    public static Meta fromJson( final MetaJson json )
    {
        return Meta.create().
            nodePath( NodePath.create( json.nodePath ).build() ).
            timestamp( json.getTimestamp() != null ? Instant.parse( json.getTimestamp() ) : null ).
            version( json.getVersion() != null ? NodeVersionId.from( json.getVersion() ) : null ).
            nodeState( NodeState.from( json.getNodeState() ) ).
            current( json.isCurrent() ).
            build();
    }

    public static MetaJson from( final Meta meta )
    {
        return MetaJson.create().
            nodePath( meta.getNodePath().toString() ).
            timestamp( meta.getTimestamp() != null ? meta.getTimestamp().toString() : null ).
            version( meta.getVersion() != null ? meta.getVersion().toString() : null ).
            current( meta.isCurrent() ).
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

    private String getNodeState()
    {
        return nodeState;
    }

    private boolean isCurrent()
    {
        return current;
    }

    public static final class Builder
    {
        private String nodePath;

        private String timestamp;

        private String version;

        private String nodeState;

        private boolean current;

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

        public Builder nodeState( final String val )
        {
            nodeState = val;
            return this;
        }

        public Builder current( final boolean val )
        {
            current = val;
            return this;
        }

        public MetaJson build()
        {
            return new MetaJson( this );
        }
    }
}
