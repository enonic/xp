package com.enonic.wem.repo.internal.entity;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.Assert.*;

public class SetNodeChildOrderCommandTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void order_by_name_asc()
        throws Exception
    {
        final FindNodesByParentResult result = createNodeAndReturnOrderedChildren( NodeIndexPath.NAME, OrderExpr.Direction.ASC );

        String previousName = "";

        for ( final Node n : result.getNodes() )
        {
            final boolean smallerThanPreviousName = previousName.compareTo( n.name().toString() ) < 0;
            assertTrue( Objects.equals( previousName, "" ) || smallerThanPreviousName );
            previousName = n.name().toString();
        }
    }

    @Test
    public void order_by_name_desc()
        throws Exception
    {
        final FindNodesByParentResult result = createNodeAndReturnOrderedChildren( NodeIndexPath.NAME, OrderExpr.Direction.DESC );

        String previousName = "";

        for ( final Node n : result.getNodes() )
        {
            final boolean largerThanPreviousName = previousName.compareTo( n.name().toString() ) > 0;
            assertTrue( Objects.equals( previousName, "" ) || largerThanPreviousName );
            previousName = n.name().toString();
        }
    }

    @Test
    public void order_by_data_value()
        throws Exception
    {
        final FindNodesByParentResult result =
            createNodeAndReturnOrderedChildren( IndexPath.from( "displayName" ), OrderExpr.Direction.ASC );

        String previousName = "";

        for ( final Node n : result.getNodes() )
        {
            final boolean smallerThanPreviousName = previousName.compareTo( n.name().toString() ) < 0;
            assertTrue( Objects.equals( previousName, "" ) || smallerThanPreviousName );
            previousName = n.name().toString();
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

        for ( final Node n : result.getNodes() )
        {
            assertTrue( "Wrong orderValue, previousOrderValue = " + previousOrderValue + ", current = " + n.getManualOrderValue(),
                        previousOrderValue == null || n.getManualOrderValue() > previousOrderValue );

            previousOrderValue = n.getManualOrderValue();
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

        for ( final Node n : result.getNodes() )
        {
            final boolean largerThanPreviousName = previousName.compareTo( n.name().toString() ) < 0;
            assertTrue( "Wrong value, previousValue = " + previousName + ", current = " + n.name(),
                        Objects.equals( previousName, "" ) || largerThanPreviousName );

            assertTrue( "Wrong orderValue, previousOrderValue = " + previousOrderValue + ", current = " + n.getManualOrderValue(),
                        previousOrderValue == null || n.getManualOrderValue() < previousOrderValue );

            previousOrderValue = n.getManualOrderValue();

            previousName = n.name().toString();
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
        Assert.assertTrue( createRightChecked );

        // Tests the correct behaviour if the right is granted
        setChildOrder( createGrantedNode, ChildOrder.create().add(
            FieldOrderExpr.create( NodeIndexPath.MANUAL_ORDER_VALUE, OrderExpr.Direction.ASC ) ).build() );
    }

    private void setChildOrder( final Node node, final ChildOrder childOrder )
    {
        SetNodeChildOrderCommand.create().
            nodeId( node.id() ).
            childOrder( childOrder ).
            nodeDao( nodeDao ).
            branchService( branchService ).
            indexServiceInternal( indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    private FindNodesByParentResult createNodeAndReturnOrderedChildren( final IndexPath path, final OrderExpr.Direction direction )
    {
        final Node node = createParentNode();

        createChildNodes( node );

        refresh();

        SetNodeChildOrderCommand.create().
            nodeId( node.id() ).
            childOrder( ChildOrder.create().add( FieldOrderExpr.create( path, direction ) ).build() ).
            nodeDao( nodeDao ).
            branchService( branchService ).
            indexServiceInternal( indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        refresh();

        return findChildren( node );
    }

    private FindNodesByParentResult findChildren( final Node node )
    {
        return findByParent( FindNodesByParentParams.create().
            parentPath( node.path() ).
            build() );
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
