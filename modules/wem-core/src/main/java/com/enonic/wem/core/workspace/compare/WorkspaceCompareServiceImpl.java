package com.enonic.wem.core.workspace.compare;

import javax.inject.Inject;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.CompareStatus;
import com.enonic.wem.api.entity.EntityComparison;
import com.enonic.wem.api.entity.EntityComparisons;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.version.VersionEntry;
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
            final EntityComparison entityComparison = doCompareEntity( entityId, sourceWorkspace, targetWorkspace );

            builder.add( entityComparison );
        }

        return builder.build();
    }

    @Override
    public EntityComparisons compare( final CompareEntitiesQuery query )
    {
        final EntityComparisons.Builder builder = EntityComparisons.create();

        final Workspace source = query.getSource();
        final Workspace target = query.getTarget();

        for ( final EntityId entityId : query.getEntityIds() )
        {
            builder.add( doCompareEntity( entityId, source, target ) );
        }

        return builder.build();
    }

    @Override
    public EntityComparison compare( final CompareEntityQuery query )
    {
        return doCompareEntity( query.getEntityId(), query.getSource(), query.getTarget() );
    }


    private EntityComparison doCompareEntity( final EntityId entityId, final Workspace sourceWorkspace, final Workspace targetWorkspace )
    {
        final BlobKey sourceBlobKey = workspaceService.getById( new WorkspaceIdQuery( sourceWorkspace, entityId ) );
        final BlobKey targetBlobKey = workspaceService.getById( new WorkspaceIdQuery( targetWorkspace, entityId ) );

        final VersionEntry sourceVersion = getVersion( sourceBlobKey );
        final VersionEntry targetVersion = getVersion( targetBlobKey );

        final CompareStatus compareStatus = DiffStatusResolver.resolve( new DiffStatusParams( sourceVersion, targetVersion ) );

        return new EntityComparison( entityId, compareStatus );
    }

    private VersionEntry getVersion( final BlobKey blobKey )
    {
        if ( blobKey == null )
        {
            return null;
        }

        return versionService.getVersion( blobKey );
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
