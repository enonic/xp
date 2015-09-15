package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersion;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;

public class GetNodeByIdCommand
    extends AbstractNodeCommand
{
    private final NodeId id;

    private GetNodeByIdCommand( final Builder builder )
    {
        super( builder );
        id = builder.id;
    }

    public Node execute()
    {
        final Context context = ContextAccessor.current();

        final NodeBranchVersion nodeBranchVersion = this.branchService.get( id, InternalContext.from( context ) );

        if ( nodeBranchVersion == null )
        {
            return null;
        }

        final NodeVersionId currentVersion = nodeBranchVersion.getVersionId();

        if ( currentVersion == null )
        {
            return null;
        }

        final Node node = nodeDao.getByVersionId( currentVersion );

        if ( node == null || !canRead( node ) )
        {
            return null;
        }

        return node;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder id( NodeId id )
        {
            this.id = id;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.id );
        }

        public GetNodeByIdCommand build()
        {
            this.validate();
            return new GetNodeByIdCommand( this );
        }
    }
}
