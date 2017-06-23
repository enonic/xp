package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StoreNodeVersionParams;

public class ImportNodeVersionCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeVersion nodeVersion;

    private ImportNodeVersionCommand( final Builder builder )
    {
        super( builder );
        nodeId = builder.nodeId;
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        nodeVersion = builder.nodeVersion;
    }

    public void execute()
    {
        this.nodeStorageService.storeVersion( StoreNodeVersionParams.create().
            nodeId( this.nodeId ).
            nodePath( this.nodePath ).
            nodeVersion( this.nodeVersion ).
            timestamp( this.timestamp ).
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

        private Builder()
        {
        }

        public ImportNodeVersionCommand build()
        {
            return new ImportNodeVersionCommand( this );
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
    }
}
