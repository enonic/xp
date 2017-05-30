package com.enonic.xp.core.impl.dump.model;

import java.time.Instant;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;

public class Meta
{
    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeVersionId version;

    private Meta( final Builder builder )
    {
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        version = builder.version;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public NodeVersionId getVersion()
    {
        return version;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePath nodePath;

        private Instant timestamp;

        private NodeVersionId version;

        private Builder()
        {
        }

        public Builder nodePath( final NodePath val )
        {
            nodePath = val;
            return this;
        }

        public Builder timestamp( final Instant val )
        {
            timestamp = val;
            return this;
        }

        public Builder version( final NodeVersionId val )
        {
            version = val;
            return this;
        }

        public Meta build()
        {
            return new Meta( this );
        }
    }
}