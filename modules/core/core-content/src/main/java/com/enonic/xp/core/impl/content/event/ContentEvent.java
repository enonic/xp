package com.enonic.xp.core.impl.content.event;

import java.time.Duration;
import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.RepositoryId;

final class ContentEvent
{

    private final NodeId nodeId;

    private final NodePath nodePath;

    private final ContentEventType type;

    private final Instant time;

    private final NodeVersionId nodeVersionId;

    private final RepositoryId repositoryId;

    private boolean valid;

    public ContentEvent( final Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.nodePath = builder.nodePath;
        this.time = builder.time;
        this.type = builder.type;
        this.nodeVersionId = builder.nodeVersionId;
        this.valid = true;
        this.repositoryId = ContextAccessor.current().getRepositoryId();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId invalidate()
    {
        this.valid = false;
        return nodeId;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public boolean isValid()
    {
        return valid;
    }

    public ContentEventType getType()
    {
        return type;
    }

    public Instant getTime()
    {
        return time;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public Duration timeToRun( final Instant now )
    {
        return Duration.between( now, time );
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodePath nodePath;

        private Instant time;

        private ContentEventType type;

        private NodeVersionId nodeVersionId;


        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder time( final Instant time )
        {
            this.time = time;
            return this;
        }

        public Builder type( final ContentEventType type )
        {
            this.type = type;
            return this;
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( nodeId, "nodeId must be set" );
            Preconditions.checkNotNull( nodePath, "nodePath must be set" );
            Preconditions.checkNotNull( type, "type must be set" );
            Preconditions.checkNotNull( time, "time must be set" );
            Preconditions.checkNotNull( nodeVersionId, "nodeVersionId must be set" );
        }

        public ContentEvent build()
        {
            validate();
            return new ContentEvent( this );
        }
    }
}
