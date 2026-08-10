package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
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
