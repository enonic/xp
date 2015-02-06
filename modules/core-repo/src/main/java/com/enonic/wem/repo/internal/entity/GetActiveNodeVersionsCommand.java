package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.branch.Branches;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.GetActiveNodeVersionsResult;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.version.VersionService;

public class GetActiveNodeVersionsCommand
    extends AbstractNodeCommand
{
    private final Branches branches;

    private final NodeId nodeId;

    private final VersionService versionService;

    private GetActiveNodeVersionsCommand( final Builder builder )
    {
        super( builder );
        this.branches = builder.branches;
        this.versionService = builder.versionService;
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

            final NodeVersionId nodeVersionId = this.queryService.get( this.nodeId, IndexContext.create().
                branch( branch ).
                repositoryId( context.getRepositoryId() ).
                authInfo( context.getAuthInfo() ).
                build() );

            if ( nodeVersionId != null )
            {
                builder.add( branch, this.versionService.getVersion( nodeVersionId, context.getRepositoryId() ) );
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

        void validate()
        {
            Preconditions.checkNotNull( this.nodeId );
            Preconditions.checkNotNull( this.branches );
            Preconditions.checkNotNull( this.versionService );
            Preconditions.checkNotNull( this.nodeDao );
        }

        public GetActiveNodeVersionsCommand build()
        {
            this.validate();
            return new GetActiveNodeVersionsCommand( this );
        }
    }
}
