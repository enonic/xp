package com.enonic.wem.core.workspace.compare;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.UnmodifiableIterator;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.CompareStatus;
import com.enonic.wem.api.entity.EntityComparison;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.version.VersionBranch;
import com.enonic.wem.core.version.VersionBranchQuery;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.compare.query.CompareEntityQuery;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

import static org.junit.Assert.*;

public class WorkspaceDiffServiceImplTest
{
    private final WorkspaceService workspaceService = Mockito.mock( WorkspaceService.class );

    private final VersionService versionService = Mockito.mock( VersionService.class );

    private final WorkspaceCompareServiceImpl diffService = new WorkspaceCompareServiceImpl();

    private final Workspace SOURCE = new Workspace( "source" );

    private final Workspace TARGET = new Workspace( "target" );

    @Before
    public void setUp()
        throws Exception
    {
        diffService.setVersionService( versionService );
        diffService.setWorkspaceService( workspaceService );
    }

    @Test
    public void workspaceDifferences()
        throws Exception
    {
        final CompareWorkspacesQuery sourceTargetQuery = new CompareWorkspacesQuery( SOURCE, TARGET );

        final EntityId ID_1 = EntityId.from( "1" );
        final EntityId ID_2 = EntityId.from( "2" );
        final EntityId ID_3 = EntityId.from( "3" );

        Mockito.when( workspaceService.getEntriesWithDiff( sourceTargetQuery ) ).
            thenReturn( EntityIds.from( ID_1, ID_2, ID_3 ) );

        // Different branch in source and target
        final BlobKey BK_SOURCE_1 = new BlobKey( "1.1.4" );
        final BlobKey BK_TARGET_1 = new BlobKey( "1.2.2" );

        // Only in source
        final BlobKey BK_SOURCE_2 = new BlobKey( "2.1.2" );

        // Same branch, source is newer
        final BlobKey BK_SOURCE_3 = new BlobKey( "3.1.1" );
        final BlobKey BK_TARGET_3 = new BlobKey( "3.1" );

        Mockito.when( workspaceService.getById( new WorkspaceIdQuery( SOURCE, ID_1 ) ) ).
            thenReturn( BK_SOURCE_1 );
        Mockito.when( workspaceService.getById( new WorkspaceIdQuery( TARGET, ID_1 ) ) ).
            thenReturn( BK_TARGET_1 );
        Mockito.when( workspaceService.getById( new WorkspaceIdQuery( SOURCE, ID_2 ) ) ).
            thenReturn( BK_SOURCE_2 );
        Mockito.when( workspaceService.getById( new WorkspaceIdQuery( SOURCE, ID_3 ) ) ).
            thenReturn( BK_SOURCE_3 );
        Mockito.when( workspaceService.getById( new WorkspaceIdQuery( TARGET, ID_3 ) ) ).
            thenReturn( BK_TARGET_3 );

        Mockito.when( versionService.getBranch( new VersionBranchQuery( BK_SOURCE_1 ) ) ).
            thenReturn( createBranchForKey( BK_SOURCE_1 ) );
        Mockito.when( versionService.getBranch( new VersionBranchQuery( BK_TARGET_1 ) ) ).
            thenReturn( createBranchForKey( BK_TARGET_1 ) );
        Mockito.when( versionService.getBranch( new VersionBranchQuery( BK_SOURCE_2 ) ) ).
            thenReturn( createBranchForKey( BK_SOURCE_2 ) );
        Mockito.when( versionService.getBranch( new VersionBranchQuery( BK_SOURCE_3 ) ) ).
            thenReturn( createBranchForKey( BK_SOURCE_3 ) );
        Mockito.when( versionService.getBranch( new VersionBranchQuery( BK_TARGET_3 ) ) ).
            thenReturn( createBranchForKey( BK_TARGET_3 ) );

        final WorkspaceComparison workspaceComparison = diffService.compareWorkspaces( sourceTargetQuery );

        assertTrue( workspaceComparison.getDiffEntries().size() == 3 );

        final UnmodifiableIterator<EntityComparison> iterator = workspaceComparison.getDiffEntries().iterator();

        assertEquals( CompareStatus.State.CONFLICT, iterator.next().getCompareStatus().getState() );
        assertEquals( CompareStatus.State.NEW, iterator.next().getCompareStatus().getState() );
        assertEquals( CompareStatus.State.NEWER, iterator.next().getCompareStatus().getState() );
    }


    @Test
    public void compare()
        throws Exception
    {
        final EntityId ID_1 = EntityId.from( "1" );
        final CompareEntityQuery compareEntityQuery = new CompareEntityQuery( ID_1, SOURCE, TARGET );

        // Different branch in source and target
        final BlobKey BK_SOURCE_1 = new BlobKey( "1.1.4" );
        final BlobKey BK_TARGET_1 = new BlobKey( "1.2.2" );

        Mockito.when( workspaceService.getById( new WorkspaceIdQuery( SOURCE, ID_1 ) ) ).
            thenReturn( BK_SOURCE_1 );
        Mockito.when( workspaceService.getById( new WorkspaceIdQuery( TARGET, ID_1 ) ) ).
            thenReturn( BK_TARGET_1 );

        Mockito.when( versionService.getBranch( new VersionBranchQuery( BK_SOURCE_1 ) ) ).
            thenReturn( createBranchForKey( BK_SOURCE_1 ) );
        Mockito.when( versionService.getBranch( new VersionBranchQuery( BK_TARGET_1 ) ) ).
            thenReturn( createBranchForKey( BK_TARGET_1 ) );

        final EntityComparison comparison = diffService.compare( compareEntityQuery );

        assertEquals( CompareStatus.State.CONFLICT, comparison.getCompareStatus().getState() );
    }

    /*
            Create VersionEntries with branchEntries. Each level indicated by '.'
            E.g: 1.2.3.4 -> 4 entries; 1.2.3.4, 1.2.3, 1.2 & 1
        */

    private VersionBranch createBranchForKey( final BlobKey key )
    {
        final VersionBranch.Builder builder = VersionBranch.create();

        doGetNextElement( key.toString(), builder );

        return builder.build();
    }

    private void doGetNextElement( final String keyAsString, final VersionBranch.Builder builder )
    {
        final boolean hasParent = keyAsString.indexOf( "." ) > 0;

        builder.add( keyAsString, hasParent ? keyAsString.substring( 0, keyAsString.lastIndexOf( "." ) ) : null );

        if ( hasParent )
        {
            doGetNextElement( keyAsString.substring( 0, keyAsString.lastIndexOf( "." ) ), builder );
        }
    }
}
