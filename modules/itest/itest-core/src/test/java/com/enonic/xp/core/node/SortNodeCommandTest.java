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
import com.enonic.xp.node.SetNodeChildOrderParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.node.SortNodeCommand;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SortNodeCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void order_by_name_asc()
        throws Exception
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
    public void order_by_name_desc()
        throws Exception
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
    public void order_by_data_value()
        throws Exception
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
    public void order_manual()
        throws Exception
    {
        final Node node = createParentNode();
        createChildNodes( node );

        setChildOrder( node, ChildOrder.create().add(
            FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.ASC ) ).build() );
        refresh();

        final FindNodesByParentResult result = findChildren( node );

        Long previousOrderValue = null;

        for ( final NodeId n : result.getNodeIds() )
        {
            final Node currentNode = getNode( n );

            assertTrue( previousOrderValue == null || currentNode.getManualOrderValue() > previousOrderValue ,
                    "Wrong orderValue, previousOrderValue = " + previousOrderValue + ", current = " + currentNode.getManualOrderValue());

            previousOrderValue = currentNode.getManualOrderValue();
        }
    }

    @Test
    public void order_manual_initial_as_previous()
        throws Exception
    {
        final Node node = createParentNode();
        createChildNodes( node );

        // Order initially by name
        setChildOrder( node, ChildOrder.create().add( FieldOrderExpr.create( NodeIndexPath.NAME, OrderExpr.Direction.ASC ) ).build() );
        refresh();

        // Now set order manual
        setChildOrder( node, ChildOrder.create().add(
            FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.DESC ) ).build() );
        refresh();

        final FindNodesByParentResult result = findChildren( node );

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
    public void order_without_permission()
        throws Exception
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

        // Tests the check of the DELETE right on the moved node
        boolean createRightChecked = false;
        try
        {
            setChildOrder( createUngrantedNode, ChildOrder.create().add(
                FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.ASC ) ).build() );
        }
        catch ( NodeAccessException e )
        {
            createRightChecked = true;
        }
        assertTrue( createRightChecked );

        // Tests the correct behaviour if the right is granted
        setChildOrder( createGrantedNode, ChildOrder.create().add(
            FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.ASC ) ).build() );
    }

    private void setChildOrder( final Node node, final ChildOrder childOrder )
    {
        SortNodeCommand.create()
            .params( SetNodeChildOrderParams.create().nodeId( node.id() ).childOrder( childOrder ).build() )
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
            .params( SetNodeChildOrderParams.create()
                         .nodeId( node.id() )
                         .childOrder( ChildOrder.create().add( FieldOrderExpr.create( path, direction ) ).build() )
                         .build() )
            .indexServiceInternal( indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        refresh();

        return findChildren( node );
    }

    private FindNodesByParentResult findChildren( final Node node )
    {
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
