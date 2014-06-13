package com.enonic.wem.core.workspace.diff;

import javax.inject.Inject;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.core.version.VersionBranch;
import com.enonic.wem.core.version.VersionBranchQuery;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.diff.query.EntityDiffQuery;
import com.enonic.wem.core.workspace.diff.query.WorkspacesDiffQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

public class WorkspaceDiffServiceImpl
    implements WorkspaceDiffService
{
    private WorkspaceService workspaceService;

    private VersionService versionService;

    @Override
    public WorkspacesDifferences getWorkspacesDifferences( final WorkspacesDiffQuery query )
    {
        final EntityIds allEntitiesWithDifference = workspaceService.getEntriesWithDiff( query );

        final WorkspacesDifferences.Builder builder = WorkspacesDifferences.create();

        for ( final EntityId entityId : allEntitiesWithDifference )
        {
            final BlobKey sourceVersion = workspaceService.getById( new WorkspaceIdQuery( query.getSource(), entityId ) );
            final BlobKey targetVersion = workspaceService.getById( new WorkspaceIdQuery( query.getTarget(), entityId ) );

            if ( sourceVersion.equals( targetVersion ) )
            {
                throw new RuntimeException( "The source and target are equal, why am I here then?!" );
            }

            final VersionBranch sourceBranch = getBranch( sourceVersion );
            final VersionBranch targetBranch = getBranch( targetVersion );

            final DiffStatus diffStatus = DiffStatusResolver.resolve( new DiffStatusParams( sourceBranch, targetBranch ) );

            builder.add( new WorkspaceDiffEntry( entityId, diffStatus ) );
        }

        return builder.build();
    }

    private VersionBranch getBranch( final BlobKey version )
    {
        final VersionBranch branch;

        if ( version != null )
        {
            branch = versionService.getBranch( new VersionBranchQuery( version ) );
        }
        else
        {
            branch = VersionBranch.create().build();
        }
        return branch;
    }

    @Override
    public void getDifferences( final EntityDiffQuery query )
    {

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
