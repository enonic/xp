package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersion;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersions;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.GetNodesByParentParams;
import com.enonic.xp.node.Nodes;

public class GetNodesByParentCommand
    extends AbstractNodeCommand
{
    private final GetNodesByParentParams params;

    private GetNodesByParentCommand( Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public Nodes execute()
    {
        final NodeBranchVersions childVersions =
            this.branchService.getChildren( params.getParentId(), InternalContext.from( ContextAccessor.current() ) );

        final Nodes.Builder builder = Nodes.create();

        for ( final NodeBranchVersion version : childVersions )
        {
            builder.add( nodeDao.getByVersionId( version.getVersionId() ) );
        }

        return builder.build();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private GetNodesByParentParams params;

        public Builder()
        {
            super();
        }

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder params( GetNodesByParentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.params );
        }

        public GetNodesByParentCommand build()
        {
            this.validate();
            return new GetNodesByParentCommand( this );
        }
    }
}
