package com.enonic.xp.core.node;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.internal.orderkey.OrderKeyCodec;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.node.SortNodeCommand;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderKeySortTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void created_node_carries_an_order_key_through_storage()
    {
        final Node created = createNode( NodePath.ROOT, "my-node" );
        assertNotNull( created.getOrderKey() );

        final Node fetched = getNodeById( created.id() );
        assertEquals( created.getOrderKey(), fetched.getOrderKey() );
        assertThat( fetched.getOrderKey() ).endsWith( "." + created.id() );
    }

    @Test
    void keyed_nodes_sort_newest_first_and_keyless_after_them_in_default_order()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );

        // keys minted at controlled instants, stored the way any version arrives from storage
        final Node keyedOld = storeWithKeyMintedAt( parent.path(), "keyed-old", 100_000 );
        final Node keyedNew = storeWithKeyMintedAt( parent.path(), "keyed-new", 200_000 );

        // a keyless node arrives exactly like a version pushed from a branch that predates order keys: stored directly,
        // never passing node creation
        final Node legacyOld = storeKeyless( parent.path(), "legacy-old", 1000 );
        final Node legacyNew = storeKeyless( parent.path(), "legacy-new", 2000 );

        refresh();

        final FindNodesByQueryResult result = doFindByQuery( NodeQuery.create()
                                                                 .parent( parent.path() )
                                                                 .setOrderExpressions(
                                                                     ChildOrder.orderKeyOrder().getOrderExpressions() )
                                                                 .build() );

        final List<String> order = result.getNodeIds().stream().map( NodeId::toString ).toList();

        assertThat( order ).containsExactly( keyedNew.id().toString(), keyedOld.id().toString(), legacyNew.id().toString(),
                                             legacyOld.id().toString() );
    }

    @Test
    void flipping_to_order_key_order_is_a_metadata_write_that_reorders_nothing_and_rewrites_no_child()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node childA = createNode( parent.path(), "child-a" );
        final Node childB = createNode( parent.path(), "child-b" );
        refresh();

        SortNodeCommand.create()
            .params( SortNodeParams.create().nodeId( parent.id() ).childOrder( ChildOrder.orderKeyOrder() ).build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
        refresh();

        assertEquals( ChildOrder.orderKeyOrder(), getNodeById( parent.id() ).getChildOrder() );
        assertEquals( childA.getNodeVersionId(), getNodeById( childA.id() ).getNodeVersionId(), "flip must not touch children" );
        assertEquals( childB.getNodeVersionId(), getNodeById( childB.id() ).getNodeVersionId(), "flip must not touch children" );

        // both children were created within one clock second, so their relative order is jitter-decided: arbitrary but
        // stable. The claims of the flip are membership, stability, and that nothing was rewritten.
        final NodeIds children = findByParent( parent.path() ).getNodeIds();
        assertThat( children ).containsExactlyInAnyOrder( childA.id(), childB.id() );
        assertEquals( children, findByParent( parent.path() ).getNodeIds(), "order must be stable across reads" );
    }

    @Test
    void imported_node_keeps_the_key_of_the_dump()
    {
        final Node original = createNode( NodePath.ROOT, "original" );
        final Node imported = this.nodeService.importNode( com.enonic.xp.node.ImportNodeParams.create()
                                                               .importNode( Node.create( original )
                                                                                .id( NodeId.from( "imported" ) )
                                                                                .name( com.enonic.xp.node.NodeName.from( "imported" ) )
                                                                                .parentPath( NodePath.ROOT )
                                                                                .build() )
                                                               .build() )
            .getNode();

        assertEquals( original.getOrderKey(), imported.getOrderKey() );
    }

    @Test
    void keyless_version_stays_keyless_through_storage()
    {
        final Node legacy = storeKeyless( NodePath.ROOT, "legacy", 1234 );
        assertNull( getNodeById( legacy.id() ).getOrderKey() );
    }

    private Node storeKeyless( final NodePath parent, final String name, final long timestampMillis )
    {
        return storeDirectly( parent, name, timestampMillis, null );
    }

    private Node storeWithKeyMintedAt( final NodePath parent, final String name, final long epochSecond )
    {
        final NodeId id = NodeId.from( name );
        final String orderKey = new OrderKeyCodec( new java.util.SplittableRandom( epochSecond ) )
            .initial( java.time.Instant.ofEpochSecond( epochSecond ), id.toString() );
        return storeDirectly( parent, name, epochSecond * 1000, orderKey );
    }

    private Node storeDirectly( final NodePath parent, final String name, final long timestampMillis, final String orderKey )
    {
        final Node node = Node.create()
            .id( NodeId.from( name ) )
            .parentPath( parent )
            .name( name )
            .timestamp( java.time.Instant.ofEpochMilli( timestampMillis ) )
            .orderKey( orderKey )
            .permissions( AccessControlList.of(
                AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() ) )
            .build();
        return this.storageService.store( StoreNodeParams.newVersion( node ),
                                          InternalContext.from( ContextAccessor.current() ) ).node();
    }
}
