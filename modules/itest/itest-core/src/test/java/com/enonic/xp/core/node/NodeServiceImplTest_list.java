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
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.ListNodesResult;
import com.enonic.xp.node.Node;
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

class NodeServiceImplTest_list
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void direct_children_ordered_by_path()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node childB = createNode( parent.path(), "b" );
        final Node childA = createNode( parent.path(), "a" );
        createNode( childA.path(), "grandchild" );
        createNode( NodePath.ROOT, "outside" );
        nodeService.refresh( RefreshMode.STORAGE );

        final ListNodesResult result =
            nodeService.list( ListNodesParams.create().parentPath( parent.path() ).build() );

        assertThat( result.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( childA.id(), childB.id() );
        assertThat( result.getEntries() ).extracting( NodeListEntry::nodePath )
            .containsExactly( childA.path(), childB.path() );
    }

    @Test
    void recursive_lists_whole_subtree()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node childA = createNode( parent.path(), "a" );
        final Node childB = createNode( parent.path(), "b" );
        final Node grandchild = createNode( childA.path(), "grandchild" );
        nodeService.refresh( RefreshMode.STORAGE );

        final ListNodesResult result =
            nodeService.list( ListNodesParams.create().parentPath( parent.path() ).recursive( true ).build() );

        assertThat( result.getEntries() ).extracting( NodeListEntry::nodeId )
            .containsExactly( childA.id(), grandchild.id(), childB.id() );
    }

    @Test
    void sees_writes_that_refreshed_storage_alone()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        // storage refresh only - no search refresh anywhere, so a query would still be blind to this node
        final Node child = createNode(
            CreateNodeParams.create().name( "just-created" ).parent( parent.path() ).refresh( RefreshMode.STORAGE ).build() );

        final ListNodesResult result =
            nodeService.list( ListNodesParams.create().parentPath( parent.path() ).build() );

        assertThat( result.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( child.id() );
    }

    @Test
    void entries_without_read_permission_are_filtered()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node visible = createNode( parent.path(), "visible" );
        createNode( CreateNodeParams.create()
                        .name( "hidden" )
                        .parent( parent.path() )
                        .permissions( denyReadForPrincipal( TEST_DEFAULT_USER.getKey() ) )
                        .build() );
        nodeService.refresh( RefreshMode.STORAGE );

        final ListNodesParams params = ListNodesParams.create().parentPath( parent.path() ).build();

        assertThat( nodeService.list( params ).getEntries() ).extracting( NodeListEntry::nodeId )
            .containsExactly( visible.id() );
        assertEquals( 2, NodeHelper.runAsAdmin( () -> nodeService.list( params ) ).getSize() );
    }

    private static AccessControlList denyReadForPrincipal( final PrincipalKey principalKey )
    {
        return AccessControlList.of( AccessControlEntry.create().deny( Permission.READ ).principal( principalKey ).build() );
    }

    @Test
    void batches_add_up_to_the_whole_listing()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node childA = createNode( parent.path(), "a" );
        // a mixed-case name, so a cursor taken from it only continues the listing when compared the way the index compares paths
        final Node childB = createNode( CreateNodeParams.create().name( "B-Mixed-Case" ).parent( parent.path() ).build() );
        final Node childC = createNode( parent.path(), "c" );
        final Node childD = createNode( parent.path(), "d" );
        final Node childE = createNode( parent.path(), "e" );
        nodeService.refresh( RefreshMode.STORAGE );

        final ListNodesResult first =
            nodeService.list( ListNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).build() );
        assertThat( first.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( childA.id(), childB.id() );
        assertNotNull( first.getCursor() );

        final ListNodesResult second = nodeService.list(
            ListNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).cursor( first.getCursor() ).build() );
        assertThat( second.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( childC.id(), childD.id() );
        assertNotNull( second.getCursor() );

        final ListNodesResult third = nodeService.list(
            ListNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).cursor( second.getCursor() ).build() );
        assertThat( third.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( childE.id() );
        assertNull( third.getCursor() );
    }

    @Test
    void an_empty_batch_still_continues_the_listing()
    {
        // a parent whose first children hide whole subtrees: a non-recursive listing discards the deeper entries after the scan,
        // so a small batch may come back with no entries at all while the listing is far from finished
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node childA = createNode( parent.path(), "a" );
        for ( int i = 1; i <= 5; i++ )
        {
            createNode( childA.path(), "grandchild-" + i );
        }
        final Node childB = createNode( parent.path(), "b" );
        nodeService.refresh( RefreshMode.STORAGE );

        final List<NodeListEntry> collected = new ArrayList<>();
        String cursor = null;
        int emptyBatches = 0;
        do
        {
            final ListNodesResult batch = nodeService.list(
                ListNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).cursor( cursor ).build() );
            if ( batch.isEmpty() )
            {
                emptyBatches++;
            }
            collected.addAll( batch.getEntries() );
            cursor = batch.getCursor();
        }
        while ( cursor != null );

        assertThat( collected ).extracting( NodeListEntry::nodeId ).containsExactly( childA.id(), childB.id() );
        assertTrue( emptyBatches > 0, "expected the grandchild-only batches to come back empty" );
    }

    @Test
    void deleting_the_listed_entries_does_not_disturb_the_batches()
    {
        // the vacuum pattern: consume a batch, delete every entry it held, continue with the cursor - without any refresh in between,
        // since the cursor only moves forward over ground the deletions leave behind
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
            final ListNodesResult batch = nodeService.list(
                ListNodesParams.create().parentPath( parent.path() ).batchSize( 3 ).cursor( cursor ).build() );
            for ( final NodeListEntry entry : batch.getEntries() )
            {
                assertTrue( seen.add( entry.nodePath() ), "entry listed twice: " + entry.nodePath() );
                nodeService.delete( DeleteNodeParams.create().nodeId( entry.nodeId() ).build() );
            }
            cursor = batch.getCursor();
        }
        while ( cursor != null );

        assertEquals( 7, seen.size() );
        // the deletions above never refreshed, as the contract of list demands of a reader; a check that they all took effect must
        nodeService.refresh( RefreshMode.STORAGE );
        assertTrue( NodeHelper.runAsAdmin(
            () -> nodeService.list( ListNodesParams.create().parentPath( parent.path() ).build() ) ).isEmpty() );
    }

    @Test
    void entries_hidden_by_permission_still_advance_the_cursor()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node first = createNode( parent.path(), "a-visible" );
        createNode( CreateNodeParams.create()
                        .name( "b-hidden" )
                        .parent( parent.path() )
                        .permissions( denyReadForPrincipal( TEST_DEFAULT_USER.getKey() ) )
                        .build() );
        final Node last = createNode( parent.path(), "c-visible" );
        nodeService.refresh( RefreshMode.STORAGE );

        final ListNodesResult firstBatch =
            nodeService.list( ListNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).build() );
        // the hidden entry is dropped from the batch but not from the scan, so the batch shrinks rather than backfills
        assertThat( firstBatch.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( first.id() );
        assertNotNull( firstBatch.getCursor() );

        final ListNodesResult secondBatch = nodeService.list(
            ListNodesParams.create().parentPath( parent.path() ).batchSize( 2 ).cursor( firstBatch.getCursor() ).build() );
        assertThat( secondBatch.getEntries() ).extracting( NodeListEntry::nodeId ).containsExactly( last.id() );
    }

    @Test
    void parent_that_does_not_exist_lists_nothing()
    {
        final ListNodesResult result =
            nodeService.list( ListNodesParams.create().parentPath( new NodePath( "/no-such-parent" ) ).build() );

        assertTrue( result.isEmpty() );
    }

    @Test
    void root_lists_top_level_nodes()
    {
        final Node top = createNode( NodePath.ROOT, "top" );
        createNode( top.path(), "below" );
        nodeService.refresh( RefreshMode.STORAGE );

        final ListNodesResult result = nodeService.list( ListNodesParams.create().parentPath( NodePath.ROOT ).build() );

        assertThat( result.getEntries() ).extracting( NodeListEntry::nodeId ).contains( top.id() );
        assertThat( result.getEntries() ).extracting( NodeListEntry::nodePath ).allMatch( path -> path.getParentPath().isRoot() );
    }
}
