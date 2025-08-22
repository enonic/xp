package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;

public class GetActiveNodeVersionsCommand
    extends AbstractNodeCommand
{
    private final Branches branches;

    private final NodeId nodeId;

    private GetActiveNodeVersionsCommand( final Builder builder )
    {
        super( builder );
        this.branches = builder.branches;
        this.nodeId = builder.nodeId;
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public GetActiveNodeVersionsResult execute()
    {
        final GetActiveNodeVersionsResult.Builder builder = GetActiveNodeVersionsResult.create();

        for ( final Branch branch : branches )
        {
            final Context context = ContextAccessor.current();

            final NodeBranchEntry nodeBranchEntry =
                this.nodeStorageService.getBranchNodeVersion( this.nodeId, InternalContext.create( context ).
                    branch( branch ).
                    build() );

            if ( nodeBranchEntry != null )
            {
                builder.add( branch, this.nodeStorageService.getVersion( nodeBranchEntry.getVersionId(),
                                                                         InternalContext.from( context ) ) );
            }
        }
        return builder.build();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Branches branches;

        private NodeId nodeId;

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder()
        {
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        @Override
        void validate()
        {
            Objects.requireNonNull( this.nodeId, "nodeId is required" );
            Objects.requireNonNull( this.branches, "branches is required" );
        }

        public GetActiveNodeVersionsCommand build()
        {
            this.validate();
            return new GetActiveNodeVersionsCommand( this );
        }
    }
}
