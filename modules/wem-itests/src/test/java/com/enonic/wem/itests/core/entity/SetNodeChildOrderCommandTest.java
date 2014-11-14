package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.repo.FindNodesByParentParams;
import com.enonic.wem.repo.FindNodesByParentResult;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodePath;
import com.enonic.wem.core.entity.SetNodeChildOrderCommand;

import static org.junit.Assert.*;

public class SetNodeChildOrderCommandTest
    extends AbstractNodeTest
{

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void order_by_name_asc()
        throws Exception
    {
        final FindNodesByParentResult result = createNodeAndReturnOrderedChildren( "name", OrderExpr.Direction.ASC );

        String previousName = "";

        for ( final Node n : result.getNodes() )
        {
            final boolean smallerThanPreviousName = previousName.compareTo( n.name().toString() ) < 0;
            assertTrue( previousName == "" || smallerThanPreviousName );
            previousName = n.name().toString();
        }
    }

    @Test
    public void order_by_name_desc()
        throws Exception
    {
        final FindNodesByParentResult result = createNodeAndReturnOrderedChildren( "name", OrderExpr.Direction.DESC );

        String previousName = "";

        for ( final Node n : result.getNodes() )
        {
            final boolean largerThanPreviousName = previousName.compareTo( n.name().toString() ) > 0;
            assertTrue( previousName == "" || largerThanPreviousName );
            previousName = n.name().toString();
        }
    }

    @Test
    public void order_by_data_value()
        throws Exception
    {
        final FindNodesByParentResult result = createNodeAndReturnOrderedChildren( "displayName", OrderExpr.Direction.ASC );

        String previousName = "";

        for ( final Node n : result.getNodes() )
        {
            final boolean smallerThanPreviousName = previousName.compareTo( n.name().toString() ) < 0;
            assertTrue( previousName == "" || smallerThanPreviousName );
            previousName = n.name().toString();
        }
    }


    @Test
    public void order_manual()
        throws Exception
    {
        final Node node = createParentNode();
        createChildNodes( node );

        setChildOrder( node, ChildOrder.create().add( FieldOrderExpr.create( "manualOrderValue", OrderExpr.Direction.ASC ) ).build() );
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
        setChildOrder( node, ChildOrder.create().add( FieldOrderExpr.create( "name", OrderExpr.Direction.ASC ) ).build() );
        refresh();

        // Now set order manual
        setChildOrder( node, ChildOrder.create().add( FieldOrderExpr.create( "manualOrderValue", OrderExpr.Direction.DESC ) ).build() );
        refresh();

        final FindNodesByParentResult result = findChildren( node );

        // Verify same order as initial ordering, name desc
        String previousName = "";
        Long previousOrderValue = null;

        for ( final Node n : result.getNodes() )
        {
            final boolean largerThanPreviousName = previousName.compareTo( n.name().toString() ) < 0;
            assertTrue( "Wrong value, previousValue = " + previousName + ", current = " + n.name(),
                        previousName == "" || largerThanPreviousName );

            assertTrue( "Wrong orderValue, previousOrderValue = " + previousOrderValue + ", current = " + n.getManualOrderValue(),
                        previousOrderValue == null || n.getManualOrderValue() < previousOrderValue );

            previousOrderValue = n.getManualOrderValue();

            previousName = n.name().toString();
        }
    }

    private void setChildOrder( final Node node, final ChildOrder childOrder )
    {
        SetNodeChildOrderCommand.create().
            nodeId( node.id() ).
            childOrder( childOrder ).
            nodeDao( nodeDao ).
            versionService( versionService ).
            workspaceService( workspaceService ).
            queryService( queryService ).
            indexService( indexService ).
            build().
            execute();
    }

    private FindNodesByParentResult createNodeAndReturnOrderedChildren( final String field, final OrderExpr.Direction direction )
    {
        final Node node = createParentNode();

        createChildNodes( node );

        refresh();

        final Node updatedNode = SetNodeChildOrderCommand.create().
            nodeId( node.id() ).
            childOrder( ChildOrder.create().add( FieldOrderExpr.create( field, direction ) ).build() ).
            nodeDao( nodeDao ).
            versionService( versionService ).
            workspaceService( workspaceService ).
            queryService( queryService ).
            indexService( indexService ).
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

        final RootDataSet data = new RootDataSet();
        data.setProperty( "displayName", Value.newString( name ) );

        return createNode( CreateNodeParams.create().
            name( name ).
            parent( parent ).
            data( data ).
            build() );
    }

}
