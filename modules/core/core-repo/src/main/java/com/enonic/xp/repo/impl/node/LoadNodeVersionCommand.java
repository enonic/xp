package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StoreNodeVersionParams;

public class LoadNodeVersionCommand
    extends AbstractNodeCommand
{
    private final Node node;

    private final NodeCommitId nodeCommitId;

    private final Attributes attributes;

    private LoadNodeVersionCommand( final Builder builder )
    {
        super( builder );
        node = builder.node;
        nodeCommitId = builder.nodeCommitId;
        attributes = builder.attributes;
    }

    public void execute()
    {
        this.nodeStorageService.storeVersion( StoreNodeVersionParams.create().
            nodeId( this.node.id() ).
            nodePath( this.node.path() ).
            nodeVersion( NodeStoreVersion.from( this.node ) ).
            nodeVersionId( this.node.getNodeVersionId() ).
            nodeCommitId( this.nodeCommitId ).
            timestamp( this.node.getTimestamp() ).
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
        private Node node;

        private NodeCommitId nodeCommitId;

        private Attributes attributes;

        private Builder()
        {
        }

        public LoadNodeVersionCommand build()
        {
            return new LoadNodeVersionCommand( this );
        }

        public Builder node( final Node val )
        {
            node = val;
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
