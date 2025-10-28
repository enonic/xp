package com.enonic.xp.core.node;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.node.SortNodeResult;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.node.SortNodeCommand;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortNodeCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void order_by_name_asc()
    {
        final FindNodesByParentResult result = createNodeAndReturnOrderedChildren( NodeIndexPath.NAME, OrderExpr.Direction.ASC );

        String previousName = "";

        for ( final NodeId n : result.getNodeIds() )
        {
            final Node node = getNode( n );
            final boolean smallerThanPreviousName = previousName.compareTo( node.name().toString() ) < 0;
            assertTrue( Objects.equals( previousName, "" ) || smallerThanPreviousName );
            previousName = node.name().toString();
        }
    }

    @Test
    void order_by_name_desc()
    {
        final FindNodesByParentResult result = createNodeAndReturnOrderedChildren( NodeIndexPath.NAME, OrderExpr.Direction.DESC );

        String previousName = "";

        for ( final NodeId n : result.getNodeIds() )
        {
            final Node node = getNode( n );
            final boolean largerThanPreviousName = previousName.compareTo( node.name().toString() ) > 0;
            assertTrue( Objects.equals( previousName, "" ) || largerThanPreviousName );
            previousName = node.name().toString();
        }
    }

    @Test
    void order_by_data_value()
    {
        final FindNodesByParentResult result =
            createNodeAndReturnOrderedChildren( IndexPath.from( "displayName" ), OrderExpr.Direction.ASC );

        String previousName = "";

        for ( final NodeId n : result.getNodeIds() )
        {
            final Node node = getNode( n );
            final boolean smallerThanPreviousName = previousName.compareTo( node.name().toString() ) < 0;
            assertTrue( Objects.equals( previousName, "" ) || smallerThanPreviousName );
            previousName = node.name().toString();
        }
    }


    @Test
    void order_manual()
    {
        final Node node = createParentNode();
        createChildNodes( node );

        final SortNodeResult sortNodeResult = setChildOrder( node, ChildOrder.manualOrder() );

        assertEquals( node.id(), sortNodeResult.getNode().id() );

        refresh();
        final FindNodesByParentResult result = findByParent( node.path() );

        Long previousOrderValue = Long.MAX_VALUE;

        for ( final NodeId n : result.getNodeIds() )
        {
            final Long manualOrderValue = getNode( n ).getManualOrderValue();

            assertThat( manualOrderValue ).isLessThan( previousOrderValue );
            previousOrderValue = manualOrderValue;
        }
        assertThat( result.getNodeIds() ).containsExactlyInAnyOrderElementsOf( sortNodeResult.getReorderedNodes().getIds() );
    }

    @Test
    void order_manual_initial_as_previous()
    {
        final Node node = createParentNode();
        createChildNodes( node );

        // Order initially by name
        setChildOrder( node, ChildOrder.create().add( FieldOrderExpr.create( NodeIndexPath.NAME, OrderExpr.Direction.ASC ) ).build() );
        refresh();

        // Now set order manual
        setChildOrder( node, ChildOrder.manualOrder() );

        refresh();
        final FindNodesByParentResult result = findByParent( node.path() );

        // Verify same order as initial ordering, name desc
        String previousName = "";
        Long previousOrderValue = null;

        for ( final NodeId n : result.getNodeIds() )
        {
            final Node currentNode = getNode( n );

            final boolean largerThanPreviousName = previousName.compareTo( currentNode.name().toString() ) < 0;
            assertTrue( Objects.equals( previousName, "" ) || largerThanPreviousName ,
                    "Wrong value, previousValue = " + previousName + ", current = " + currentNode.name());

            assertTrue( previousOrderValue == null || currentNode.getManualOrderValue() < previousOrderValue,
                    "Wrong orderValue, previousOrderValue = " + previousOrderValue + ", current = " + currentNode.getManualOrderValue());

            previousOrderValue = currentNode.getManualOrderValue();

            previousName = currentNode.name().toString();
        }
    }

    @Test
    void order_without_permission()
    {
        final Node createUngrantedNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( AccessControlList.of(
                AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().deny( Permission.CREATE ).build() ) ).
            build() );

        final Node createGrantedNode = createNode( CreateNodeParams.create().
            name( "my-node2" ).
            parent( NodePath.ROOT ).
            permissions( AccessControlList.of( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() ) ).
            build() );

        assertThrows( NodeAccessException.class, () -> setChildOrder( createUngrantedNode, ChildOrder.manualOrder() ) );

        // Tests the correct behaviour if the right is granted
        setChildOrder( createGrantedNode, ChildOrder.manualOrder() );
    }

    private SortNodeResult setChildOrder( final Node node, final ChildOrder childOrder )
    {
        return SortNodeCommand.create()
            .params( SortNodeParams.create().nodeId( node.id() ).childOrder( childOrder ).build() )
            .indexServiceInternal( indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
    }

    private FindNodesByParentResult createNodeAndReturnOrderedChildren( final IndexPath path, final OrderExpr.Direction direction )
    {
        final Node node = createParentNode();

        createChildNodes( node );

        refresh();

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( node.id() )
                         .childOrder( ChildOrder.create().add( FieldOrderExpr.create( path, direction ) ).build() )
                         .build() )
            .indexServiceInternal( indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        refresh();
        return findByParent( node.path() );
    }

    private void createChildNodes( final Node node )
    {
        createMinimalNode( "b", node.path() );
        createMinimalNode( "a", node.path() );
        createMinimalNode( "c", node.path() );
        createMinimalNode( "f", node.path() );
        createMinimalNode( "e", node.path() );
        createMinimalNode( "d", node.path() );
    }

    private Node createParentNode()
    {
        return createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );
    }

    private Node createMinimalNode( final String name, final NodePath parent )
    {

        final PropertyTree data = new PropertyTree();
        data.setString( "displayName", name );

        return createNode( CreateNodeParams.create().
            name( name ).
            parent( parent ).
            data( data ).
            build() );
    }

}
