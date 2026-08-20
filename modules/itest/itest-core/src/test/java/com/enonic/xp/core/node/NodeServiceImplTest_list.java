package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.ListNodesParams;
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
import static org.assertj.core.groups.Tuple.tuple;
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
    void lists_whole_subtree_ordered_by_path()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node childB = createNode( parent.path(), "b" );
        final Node childA = createNode( parent.path(), "a" );
        final Node grandchild = createNode( childA.path(), "grandchild" );
        createNode( NodePath.ROOT, "outside" );
        nodeService.refresh( RefreshMode.STORAGE );

        assertThat( nodeService.list( ListNodesParams.create().parentPath( parent.path() ).build() ) )
            .extracting( NodeListEntry::nodeId, NodeListEntry::nodePath )
            .containsExactly( tuple( childA.id(), childA.path() ),
                              tuple( grandchild.id(), grandchild.path() ),
                              tuple( childB.id(), childB.path() ) );
    }

    @Test
    void sees_writes_that_refreshed_storage_alone()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        // storage refresh only - no search refresh anywhere, so a query would still be blind to this node
        final Node child = createNode(
            CreateNodeParams.create().name( "just-created" ).parent( parent.path() ).refresh( RefreshMode.STORAGE ).build() );

        assertThat( nodeService.list( ListNodesParams.create().parentPath( parent.path() ).build() ) )
            .extracting( NodeListEntry::nodeId )
            .containsExactly( child.id() );
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

        assertThat( nodeService.list( params ) ).extracting( NodeListEntry::nodeId ).containsExactly( visible.id() );
        assertThat( NodeHelper.runAsAdmin( () -> nodeService.list( params ).count() ) ).isEqualTo( 2 );
    }

    private static AccessControlList denyReadForPrincipal( final PrincipalKey principalKey )
    {
        return AccessControlList.of( AccessControlEntry.create().deny( Permission.READ ).principal( principalKey ).build() );
    }

    @Test
    void parent_that_does_not_exist_lists_nothing()
    {
        assertTrue( nodeService.list( ListNodesParams.create().parentPath( new NodePath( "/no-such-parent" ) ).build() )
                        .findAny()
                        .isEmpty() );
    }

    @Test
    void root_lists_the_whole_tree()
    {
        final Node top = createNode( NodePath.ROOT, "top" );
        final Node below = createNode( top.path(), "below" );
        nodeService.refresh( RefreshMode.STORAGE );

        assertThat( nodeService.list( ListNodesParams.create().parentPath( NodePath.ROOT ).build() ) )
            .extracting( NodeListEntry::nodeId )
            .contains( top.id(), below.id() );
    }
}
