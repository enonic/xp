package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StoreNodeVersionParams;

public class LoadNodeVersionCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeVersion nodeVersion;

    private final NodeVersionId nodeVersionId;

    private final NodeCommitId nodeCommitId;

    private final Attributes attributes;

    private LoadNodeVersionCommand( final Builder builder )
    {
        super( builder );
        nodeId = builder.nodeId;
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        nodeVersion = builder.nodeVersion;
        nodeVersionId = builder.nodeVersionId;
        nodeCommitId = builder.nodeCommitId;
        attributes = builder.attributes;
    }

    public void execute()
    {
        this.nodeStorageService.storeVersion( StoreNodeVersionParams.create().
            nodeId( this.nodeId ).
            nodePath( this.nodePath ).
            nodeVersion( this.nodeVersion ).
            nodeVersionId( this.nodeVersionId ).
            nodeCommitId( this.nodeCommitId ).
            timestamp( this.timestamp ).
            attributes( this.attributes ).
            build(), InternalContext.from( ContextAccessor.current() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private NodePath nodePath;

        private Instant timestamp;

        private NodeVersion nodeVersion;

        private NodeVersionId nodeVersionId;

        private NodeCommitId nodeCommitId;

        private Attributes attributes;

        private Builder()
        {
        }

        public LoadNodeVersionCommand build()
        {
            return new LoadNodeVersionCommand( this );
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
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

        public Builder nodeVersion( final NodeVersion val )
        {
            nodeVersion = val;
            return this;
        }

        public Builder nodeVersionId( final NodeVersionId val )
        {
            nodeVersionId = val;
            return this;
        }

        public Builder nodeCommitId( final NodeCommitId val )
        {
            nodeCommitId = val;
            return this;
        }

        public Builder attributes( final Attributes val )
        {
            attributes = val;
            return this;
        }
    }
}
