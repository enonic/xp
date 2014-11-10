package com.enonic.wem.core.entity;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.compare.DiffStatusParams;
import com.enonic.wem.core.workspace.compare.DiffStatusResolver;

public class CompareNodesCommand
{
    private final NodeIds nodeIds;

    private final Workspace target;

    private final QueryService queryService;

    private final VersionService versionService;

    private CompareNodesCommand( Builder builder )
    {
        nodeIds = builder.nodeIds;
        target = builder.target;
        queryService = builder.queryService;
        versionService = builder.versionService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeComparisons execute()
    {
        final NodeComparisons.Builder builder = NodeComparisons.create();

        for ( final NodeId nodeId : this.nodeIds )
        {
            final NodeComparison nodeComparison = doCompareVersions( nodeId );

            builder.add( nodeComparison );
        }

        return builder.build();
    }

    private NodeComparison doCompareVersions( final NodeId nodeId )
    {
        final Context context = Context.current();

        final NodeVersionId sourceVersionId = queryService.get( nodeId, IndexContext.from( context ) );
        final NodeVersionId targetVersionId = queryService.get( nodeId, IndexContext.from( this.target, context.getRepositoryId() ) );

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
        private NodeIds nodeIds;

        private Workspace target;

        private QueryService queryService;

        private VersionService versionService;

        private Builder()
        {
        }

        public Builder nodeId( final NodeIds nodeIds )
        {
            this.nodeIds = nodeIds;
            return this;
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder queryService( final QueryService queryService )
        {
            this.queryService = queryService;
            return this;
        }

        public Builder versionService( final VersionService versionService )
        {
            this.versionService = versionService;
            return this;
        }

        public CompareNodesCommand build()
        {
            return new CompareNodesCommand( this );
        }
    }
}
