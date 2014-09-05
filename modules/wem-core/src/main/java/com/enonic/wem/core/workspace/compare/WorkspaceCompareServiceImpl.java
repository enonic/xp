package com.enonic.wem.core.workspace.compare;

import javax.inject.Inject;

import com.enonic.wem.api.entity.CompareStatus;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NodeComparison;
import com.enonic.wem.api.entity.NodeComparisons;
import com.enonic.wem.api.entity.NodeVersion;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.compare.query.CompareEntitiesQuery;
import com.enonic.wem.core.workspace.compare.query.CompareEntityQuery;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

public class WorkspaceCompareServiceImpl
    implements WorkspaceCompareService
{
    private WorkspaceService workspaceService;

    private VersionService versionService;

    @Override
    public WorkspaceComparison compareWorkspaces( final CompareWorkspacesQuery query )
    {
        final EntityIds allEntitiesWithDifference = workspaceService.getEntriesWithDiff( query );

        final WorkspaceComparison.Builder builder = WorkspaceComparison.create();

        final Workspace sourceWorkspace = query.getSource();
        final Workspace targetWorkspace = query.getTarget();

        for ( final EntityId entityId : allEntitiesWithDifference )
        {
            final NodeComparison nodeComparison = doCompareEntity( entityId, sourceWorkspace, targetWorkspace );

            builder.add( nodeComparison );
        }

        return builder.build();
    }

    @Override
    public NodeComparisons compare( final CompareEntitiesQuery query )
    {
        final NodeComparisons.Builder builder = NodeComparisons.create();

        final Workspace source = query.getSource();
        final Workspace target = query.getTarget();

        for ( final EntityId entityId : query.getEntityIds() )
        {
            builder.add( doCompareEntity( entityId, source, target ) );
        }

        return builder.build();
    }

    @Override
    public NodeComparison compare( final CompareEntityQuery query )
    {
        return doCompareEntity( query.getEntityId(), query.getSource(), query.getTarget() );
    }


    private NodeComparison doCompareEntity( final EntityId entityId, final Workspace sourceWorkspace, final Workspace targetWorkspace )
    {
        final NodeVersionId sourceVersionId = workspaceService.getCurrentVersion( new WorkspaceIdQuery( sourceWorkspace, entityId ) );
        final NodeVersionId targetVersionId = workspaceService.getCurrentVersion( new WorkspaceIdQuery( targetWorkspace, entityId ) );

        final NodeVersion sourceVersion = getVersion( sourceVersionId );
        final NodeVersion targetVersion = getVersion( targetVersionId );

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( sourceVersion, targetVersion ) );

        return new NodeComparison( entityId, compareStatus );
    }

    private NodeVersion getVersion( final NodeVersionId nodeVersionId )
    {
        if ( nodeVersionId == null )
        {
            return null;
        }

        return versionService.getVersion( nodeVersionId );
    }

    @Inject
    public void setWorkspaceService( final WorkspaceService workspaceService )
    {
        this.workspaceService = workspaceService;
    }

    @Inject
    public void setVersionService( final VersionService versionService )
    {
        this.versionService = versionService;
    }
}
