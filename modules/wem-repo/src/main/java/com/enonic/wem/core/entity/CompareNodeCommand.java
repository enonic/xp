package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.compare.DiffStatusParams;
import com.enonic.wem.core.workspace.compare.DiffStatusResolver;
import com.enonic.wem.repo.NodeComparison;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeVersion;
import com.enonic.wem.repo.NodeVersionId;

public class CompareNodeCommand
{
    private final NodeId nodeId;

    private final Workspace target;

    private final VersionService versionService;

    private final QueryService queryService;

    private CompareNodeCommand( Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.target = builder.target;
        this.versionService = builder.versionService;
        this.queryService = builder.queryService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeComparison execute()
    {
        final Context context = ContextAccessor.current();

        final NodeVersionId sourceVersionId = this.queryService.get( nodeId, IndexContext.from( context ) );
        final NodeVersionId targetVersionId = this.queryService.get( nodeId, IndexContext.from( this.target, context.getRepositoryId() ) );

        final NodeVersion sourceVersion = getVersion( sourceVersionId, context );
        final NodeVersion targetVersion = getVersion( targetVersionId, context );

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( sourceVersion, targetVersion ) );

        return new NodeComparison( nodeId, compareStatus );
    }

    private NodeVersion getVersion( final NodeVersionId nodeVersionId, final Context context )
    {
        if ( nodeVersionId == null )
        {
            return null;
        }

        return versionService.getVersion( nodeVersionId, context.getRepositoryId() );
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private Workspace target;

        private VersionService versionService;

        private QueryService queryService;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder target( Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder queryService( final QueryService queryService )
        {
            this.queryService = queryService;
            return this;
        }

        public Builder versionService( VersionService versionService )
        {
            this.versionService = versionService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( nodeId );
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( queryService );
            Preconditions.checkNotNull( versionService );
        }

        public CompareNodeCommand build()
        {
            this.validate();
            return new CompareNodeCommand( this );
        }
    }
}