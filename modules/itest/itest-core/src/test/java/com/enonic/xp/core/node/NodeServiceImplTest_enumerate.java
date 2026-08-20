package com.enonic.xp.core.node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.EnumerateNodesParams;
import com.enonic.xp.node.EnumerateNodesResult;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.MoveNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeServiceImplTest_enumerate
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    private static AccessControlList denyReadForPrincipal( final PrincipalKey principalKey )
    {
        return AccessControlList.of( AccessControlEntry.create().deny( Permission.READ ).principal( principalKey ).build() );
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
            nodeService.enumerate( EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).build() );
        assertThat( first.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( childA.id(), childB.id() );
        assertNotNull( first.getCursor() );

        final EnumerateNodesResult second = nodeService.enumerate(
            EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).cursor( first.getCursor() ).build() );
        assertThat( second.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( childC.id(), childD.id() );
        assertNotNull( second.getCursor() );

        final EnumerateNodesResult third = nodeService.enumerate(
            EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).cursor( second.getCursor() ).build() );
        assertThat( third.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( childE.id() );
        assertNull( third.getCursor() );
    }

    @Test
    void an_empty_batch_still_continues_the_enumeration()
    {
        // hidden entries are dropped after the scan, so a small batch may come back with no entries at all while the enumeration is far
        // from finished; the hidden ids sort between the visible ones here, so the middle batches hold nothing
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node childA = createNode( parent.path(), "a" );
        for ( int i = 1; i <= 5; i++ )
        {
            createNode( CreateNodeParams.create()
                            .setNodeId( NodeId.from( "b-hidden-" + i ) )
                            .name( "b-hidden-" + i )
                            .parent( parent.path() )
                            .permissions( denyReadForPrincipal( TEST_DEFAULT_USER.getKey() ) )
                            .build() );
        }
        final Node childB = createNode( parent.path(), "z" );
        nodeService.refresh( RefreshMode.STORAGE );

        final List<NodeListEntry> collected = new ArrayList<>();
        String cursor = null;
        int emptyBatches = 0;
        do
        {
            final EnumerateNodesResult batch = nodeService.enumerate(
                EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).cursor( cursor ).build() );
            if ( batch.getEntries().isEmpty() )
            {
                emptyBatches++;
            }
            collected.addAll( batch.getEntries() );
            cursor = batch.getCursor();
        }
        while ( cursor != null );

        assertThat( collected ).extracting( NodeListEntry::nodeId ).containsExactly( childA.id(), childB.id() );
        assertTrue( emptyBatches > 0, "expected the hidden-only batches to come back empty" );
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

        final EnumerateNodesResult first = nodeService.enumerate( params.build() );
        assertThat( first.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( childA.id(), childB.id() );

        nodeService.move( MoveNodeParams.create().nodeId( childD.id() ).newParentPath( childA.path() ).build() );
        nodeService.refresh( RefreshMode.STORAGE );

        final EnumerateNodesResult second = nodeService.enumerate( params.cursor( first.getCursor() ).build() );
        assertThat( second.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( childC.id(), childD.id() );
        assertThat( second.getEntries() ).extracting( NodeListEntry::nodePath )
            .contains( new NodePath( childA.path(), childD.name() ) );

        final EnumerateNodesResult third = nodeService.enumerate( params.cursor( second.getCursor() ).build() );
        assertThat( third.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( childE.id() );
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
            final EnumerateNodesResult batch = nodeService.enumerate(
                EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 3 ).cursor( cursor ).build() );
            for ( final NodeListEntry entry : batch.getEntries() )
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

    @Test
    void entries_hidden_by_permission_still_advance_the_cursor()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node first = createNode( parent.path(), "a-visible" );
        // the id is what places an entry in the scan, so it is set explicitly to put the hidden entry between the visible ones
        createNode( CreateNodeParams.create()
                        .setNodeId( NodeId.from( "b-hidden" ) )
                        .name( "b-hidden" )
                        .parent( parent.path() )
                        .permissions( denyReadForPrincipal( TEST_DEFAULT_USER.getKey() ) )
                        .build() );
        final Node last = createNode( parent.path(), "c-visible" );
        nodeService.refresh( RefreshMode.STORAGE );

        final EnumerateNodesResult firstBatch =
            nodeService.enumerate( EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).build() );
        // the hidden entry is dropped from the batch but not from the scan, so the batch shrinks rather than backfills
        assertThat( firstBatch.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( first.id() );
        assertNotNull( firstBatch.getCursor() );

        final EnumerateNodesResult secondBatch = nodeService.enumerate(
            EnumerateNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).cursor( firstBatch.getCursor() ).build() );
        assertThat( secondBatch.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( last.id() );
    }
}
