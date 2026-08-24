package com.enonic.xp.core.node;

import java.time.Instant;
import java.util.SplittableRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.internal.orderkey.OrderKeyCodec;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.node.SortNodeCommand;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortNodeCommandTest_orderKey
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void between_anchors_places_the_child_between_them()
    {
        final Node parent = keyOrderedParent( "parent-between" );
        final Node bottom = storeWithKeyMintedAt( parent.path(), "bottom", 100_000 );
        final Node middle = storeWithKeyMintedAt( parent.path(), "middle", 200_000 );
        final Node top = storeWithKeyMintedAt( parent.path(), "top", 300_000 );
        refresh();

        reorder( parent, ReorderChildNodeParams.create()
            .nodeId( bottom.id() )
            .afterOrderKey( top.getOrderKey() )
            .beforeOrderKey( middle.getOrderKey() )
            .build() );

        assertThat( findByParent( parent.path() ).getNodeIds() ).containsExactly( top.id(), bottom.id(), middle.id() );

        final String movedKey = getNodeById( bottom.id() ).getOrderKey();
        assertNotEquals( bottom.getOrderKey(), movedKey );
        assertThat( movedKey ).endsWith( "." + bottom.id() );
    }

    @Test
    void no_anchors_means_the_top_of_the_list()
    {
        final Node parent = keyOrderedParent( "parent-first" );
        final Node bottom = storeWithKeyMintedAt( parent.path(), "bottom", 100_000 );
        final Node top = storeWithKeyMintedAt( parent.path(), "top", 200_000 );
        refresh();

        reorder( parent, ReorderChildNodeParams.create().nodeId( bottom.id() ).build() );

        assertThat( findByParent( parent.path() ).getNodeIds() ).containsExactly( bottom.id(), top.id() );
    }

    @Test
    void a_single_after_anchor_places_the_child_directly_below_it()
    {
        final Node parent = keyOrderedParent( "parent-after" );
        final Node bottom = storeWithKeyMintedAt( parent.path(), "bottom", 100_000 );
        final Node middle = storeWithKeyMintedAt( parent.path(), "middle", 200_000 );
        final Node top = storeWithKeyMintedAt( parent.path(), "top", 300_000 );
        refresh();

        reorder( parent, ReorderChildNodeParams.create().nodeId( top.id() ).afterOrderKey( bottom.getOrderKey() ).build() );

        assertThat( findByParent( parent.path() ).getNodeIds() ).containsExactly( middle.id(), bottom.id(), top.id() );
    }

    @Test
    void garbage_and_misordered_anchors_are_refused()
    {
        final Node parent = keyOrderedParent( "parent-refuse" );
        final Node childA = storeWithKeyMintedAt( parent.path(), "child-a", 100_000 );
        final Node childB = storeWithKeyMintedAt( parent.path(), "child-b", 200_000 );
        refresh();

        assertThrows( IllegalArgumentException.class, () -> reorder( parent, ReorderChildNodeParams.create()
            .nodeId( childA.id() )
            .afterOrderKey( "not a key at all" )
            .build() ) );

        assertThrows( IllegalArgumentException.class, () -> reorder( parent, ReorderChildNodeParams.create()
            .nodeId( childA.id() )
            .afterOrderKey( childA.getOrderKey() )
            .beforeOrderKey( childB.getOrderKey() )
            .build() ), "anchors out of display order must be refused" );
    }

    @Test
    void flip_and_reorder_in_one_call()
    {
        final Node parent = createNode( NodePath.ROOT, "parent-flip" );
        final Node bottom = storeWithKeyMintedAt( parent.path(), "bottom", 100_000 );
        final Node top = storeWithKeyMintedAt( parent.path(), "top", 200_000 );
        refresh();

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parent.id() )
                         .childOrder( ChildOrder.orderKeyOrder() )
                         .addManualOrder( ReorderChildNodeParams.create().nodeId( bottom.id() ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
        refresh();

        assertTrue( getNodeById( parent.id() ).getChildOrder().isOrderKeyOrder() );
        assertThat( findByParent( parent.path() ).getNodeIds() ).containsExactly( bottom.id(), top.id() );
    }

    @Test
    void the_root_node_is_key_ordered_like_any_parent()
    {
        SortNodeCommand.create()
            .params( SortNodeParams.create().nodeId( NodeId.ROOT ).childOrder( ChildOrder.orderKeyOrder() ).build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
        refresh();

        assertTrue( getNodeById( NodeId.ROOT ).getChildOrder().isOrderKeyOrder() );

        final Node bottom = storeWithKeyMintedAt( NodePath.ROOT, "root-bottom", 100_000 );
        final Node top = storeWithKeyMintedAt( NodePath.ROOT, "root-top", 200_000 );
        // the real create path: the birth key of its creation instant places it first
        final Node created = createNode( NodePath.ROOT, "root-created" );
        refresh();

        assertThat( created.getOrderKey() ).endsWith( "." + created.id() );
        assertThat( findByParent( NodePath.ROOT ).getNodeIds() ).containsExactly( created.id(), top.id(), bottom.id() );

        reorder( getNodeById( NodeId.ROOT ), ReorderChildNodeParams.create()
            .nodeId( created.id() )
            .afterOrderKey( top.getOrderKey() )
            .beforeOrderKey( bottom.getOrderKey() )
            .build() );

        assertThat( findByParent( NodePath.ROOT ).getNodeIds() ).containsExactly( top.id(), created.id(), bottom.id() );
    }

    private Node keyOrderedParent( final String name )
    {
        final Node parent = createNode( NodePath.ROOT, name );
        SortNodeCommand.create()
            .params( SortNodeParams.create().nodeId( parent.id() ).childOrder( ChildOrder.orderKeyOrder() ).build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
        return getNodeById( parent.id() );
    }

    private void reorder( final Node parent, final ReorderChildNodeParams reorderParams )
    {
        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parent.id() )
                         .childOrder( ChildOrder.orderKeyOrder() )
                         .addManualOrder( reorderParams )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
        refresh();
    }

    private Node storeWithKeyMintedAt( final NodePath parent, final String name, final long epochSecond )
    {
        final NodeId id = NodeId.from( name + "-" + parent.getName() );
        final String orderKey = new OrderKeyCodec( new SplittableRandom( epochSecond ) )
            .initial( Instant.ofEpochSecond( epochSecond ), id.toString() );
        final Node node = Node.create()
            .id( id )
            .parentPath( parent )
            .name( name )
            .timestamp( Instant.ofEpochSecond( epochSecond ) )
            .orderKey( orderKey )
            .permissions( AccessControlList.of(
                AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() ) )
            .build();
        return this.storageService.store( StoreNodeParams.newVersion( node ),
                                          InternalContext.from( ContextAccessor.current() ) ).node();
    }
}
