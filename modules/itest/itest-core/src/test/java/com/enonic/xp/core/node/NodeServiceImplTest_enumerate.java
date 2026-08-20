package com.enonic.xp.core.node;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.EnumerateNodesParams;
import com.enonic.xp.node.EnumerateNodesResult;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEnumerationEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeServiceImplTest_enumerate
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    private EnumerateNodesResult enumerate( final EnumerateNodesParams params )
    {
        return NodeHelper.runAsAdmin( () -> nodeService.enumerate( params ) );
    }

    @Test
    void batches_add_up_to_the_whole_subtree()
    {
        // an enumeration scans by node id, and the test helper derives each id from the name, so the expected order is by name
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node childA = createNode( parent.path(), "a" );
        final Node childB = createNode( parent.path(), "b" );
        final Node childC = createNode( parent.path(), "c" );
        final Node childD = createNode( parent.path(), "d" );
        final Node childE = createNode( parent.path(), "e" );
        nodeService.refresh( RefreshMode.STORAGE );

        final EnumerateNodesResult first =
            enumerate( EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).build() );
        assertThat( first.getEntries() ).extracting( NodeEnumerationEntry::nodeId ).containsExactly( childA.id(), childB.id() );
        // each entry names the version the scan observed, so a reader can hold the enumerated snapshot of the node
        assertThat( first.getEntries() ).extracting( NodeEnumerationEntry::versionId )
            .containsExactly( childA.getNodeVersionId(), childB.getNodeVersionId() );
        assertNotNull( first.getCursor() );

        final EnumerateNodesResult second = enumerate(
            EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).cursor( first.getCursor() ).build() );
        assertThat( second.getEntries() ).extracting( NodeEnumerationEntry::nodeId ).containsExactly( childC.id(), childD.id() );
        assertNotNull( second.getCursor() );

        final EnumerateNodesResult third = enumerate(
            EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).cursor( second.getCursor() ).build() );
        assertThat( third.getEntries() ).extracting( NodeEnumerationEntry::nodeId ).containsExactly( childE.id() );
        assertNull( third.getCursor() );
    }

    @Test
    void requires_the_administrator_role()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        nodeService.refresh( RefreshMode.STORAGE );

        final EnumerateNodesParams params = EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).build();

        // the default test user is no administrator: the enumeration refuses the caller up front instead of filtering for them
        assertThrows( ForbiddenAccessException.class, () -> nodeService.enumerate( params ) );
    }

    @Test
    void enumerates_without_filtering_by_permissions()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node visible = createNode( parent.path(), "visible" );
        final Node hidden = createNode( CreateNodeParams.create()
                                            .name( "hidden" )
                                            .parent( parent.path() )
                                            .permissions( AccessControlList.of( AccessControlEntry.create()
                                                                                    .deny( Permission.READ )
                                                                                    .principal( TEST_DEFAULT_USER.getKey() )
                                                                                    .build() ) )
                                            .build() );
        nodeService.refresh( RefreshMode.STORAGE );

        final EnumerateNodesResult result =
            enumerate( EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 10 ).build() );

        assertThat( result.getEntries() ).extracting( NodeEnumerationEntry::nodeId ).contains( visible.id(), hidden.id() );
    }

    @Test
    void a_move_between_batches_neither_hides_nor_repeats_an_entry()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node childA = createNode( parent.path(), "a" );
        final Node childB = createNode( parent.path(), "b" );
        final Node childC = createNode( parent.path(), "c" );
        final Node childD = createNode( parent.path(), "d" );
        final Node childE = createNode( parent.path(), "e" );
        nodeService.refresh( RefreshMode.STORAGE );

        final EnumerateNodesParams.Builder params = EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 2 );

        final EnumerateNodesResult first = enumerate( params.build() );
        assertThat( first.getEntries() ).extracting( NodeEnumerationEntry::nodeId ).containsExactly( childA.id(), childB.id() );

        nodeService.move( MoveNodeParams.create().nodeId( childD.id() ).newParentPath( childA.path() ).build() );
        nodeService.refresh( RefreshMode.STORAGE );

        final EnumerateNodesResult second = enumerate( params.cursor( first.getCursor() ).build() );
        assertThat( second.getEntries() ).extracting( NodeEnumerationEntry::nodeId ).containsExactly( childC.id(), childD.id() );
        assertThat( second.getEntries() ).extracting( NodeEnumerationEntry::nodePath )
            .contains( new NodePath( childA.path(), childD.name() ) );

        final EnumerateNodesResult third = enumerate( params.cursor( second.getCursor() ).build() );
        assertThat( third.getEntries() ).extracting( NodeEnumerationEntry::nodeId ).containsExactly( childE.id() );
        assertNull( third.getCursor() );
    }

    @Test
    void deleting_the_enumerated_entries_does_not_disturb_the_batches()
    {
        // deliberately no refresh between the batches: the cursor only moves forward over ground the deletions leave behind
        final Node parent = createNode( NodePath.ROOT, "parent" );
        for ( int i = 1; i <= 7; i++ )
        {
            createNode( parent.path(), "child-" + i );
        }
        nodeService.refresh( RefreshMode.STORAGE );

        final Set<NodePath> seen = new HashSet<>();
        String cursor = null;
        do
        {
            final EnumerateNodesResult batch = enumerate(
                EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 3 ).cursor( cursor ).build() );
            for ( final NodeEnumerationEntry entry : batch.getEntries() )
            {
                assertTrue( seen.add( entry.nodePath() ), "entry enumerated twice: " + entry.nodePath() );
                nodeService.delete( DeleteNodeParams.create().nodeId( entry.nodeId() ).build() );
            }
            cursor = batch.getCursor();
        }
        while ( cursor != null );

        assertEquals( 7, seen.size() );
        // the verifying read needs the deletions refreshed; neither list nor enumerate refreshes on its own
        nodeService.refresh( RefreshMode.STORAGE );
        assertTrue( NodeHelper.runAsAdmin(
            () -> nodeService.list( ListNodesParams.create().parentPath( parent.path() ).build() ) ).isEmpty() );
    }
}
