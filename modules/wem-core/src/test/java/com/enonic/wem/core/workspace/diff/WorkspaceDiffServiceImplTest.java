package com.enonic.wem.core.workspace.diff;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.UnmodifiableIterator;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.version.VersionBranch;
import com.enonic.wem.core.version.VersionBranchQuery;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.diff.query.WorkspacesDiffQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

import static org.junit.Assert.*;

public class WorkspaceDiffServiceImplTest
{
    private final WorkspaceService workspaceService = Mockito.mock( WorkspaceService.class );

    private final VersionService versionService = Mockito.mock( VersionService.class );

    private final WorkspaceDiffServiceImpl diffService = new WorkspaceDiffServiceImpl();

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
        final WorkspacesDiffQuery sourceTargetQuery = new WorkspacesDiffQuery( SOURCE, TARGET );

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

        final WorkspacesDifferences workspacesDifferences = diffService.getWorkspacesDifferences( sourceTargetQuery );

        assertTrue( workspacesDifferences.getDiffEntries().size() == 3 );

        final UnmodifiableIterator<WorkspaceDiffEntry> iterator = workspacesDifferences.getDiffEntries().iterator();

        assertEquals( DiffStatus.State.CONFLICT, iterator.next().getDiffStatus().getState() );
        assertEquals( DiffStatus.State.NEW, iterator.next().getDiffStatus().getState() );
        assertEquals( DiffStatus.State.NEWER, iterator.next().getDiffStatus().getState() );
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
