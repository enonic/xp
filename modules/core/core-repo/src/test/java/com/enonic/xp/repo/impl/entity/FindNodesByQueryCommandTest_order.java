package com.enonic.xp.repo.impl.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.parser.QueryParser;

public class FindNodesByQueryCommandTest_order
    extends AbstractNodeTest
{

    private static final String FIELD_LONG = "fieldLong";

    private static final String FIELD_BOOL = "fieldBoolean";

    private static final String FIELD_STRING = "fieldString";

    private static final String PARENT_QUERY = "_parentPath=\"/\"";

    private static final String ORDER_DESC = "DESC";

    private static final String ORDER_ASC = "ASC";

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
        this.nodeInitializing();
    }

    public void nodeInitializing()
        throws Exception
    {
        createNode( "node1", createPropertyMap( 1l, "b", false ), NodePath.ROOT );
        createNode( "node2", createPropertyMap( 3l, "c", true ), NodePath.ROOT );
        createNode( "node3", createPropertyMap( 2l, "a", false ), NodePath.ROOT );
    }

    @Test
    public void testByLongSorting()
    {
        String[] orders = {FIELD_LONG + " " + ORDER_DESC};
        FindNodesByQueryResult result = sort( orders );

        Iterator<Node> iterator = result.getNodes().iterator();
        Assert.assertEquals( "node2", iterator.next().name().toString() );
        Assert.assertEquals( "node3", iterator.next().name().toString() );
        Assert.assertEquals( "node1", iterator.next().name().toString() );

    }

    @Test
    public void testByStringSorting()
    {
        String[] orders = {FIELD_STRING + " " + ORDER_ASC};
        FindNodesByQueryResult result = sort( orders );

        Iterator<Node> iterator = result.getNodes().iterator();
        Assert.assertEquals( "node3", iterator.next().name().toString() );
        Assert.assertEquals( "node1", iterator.next().name().toString() );
        Assert.assertEquals( "node2", iterator.next().name().toString() );

    }

    @Test
    public void testByBooleanSorting()
    {
        String[] orders = {FIELD_BOOL + " " + ORDER_DESC};
        FindNodesByQueryResult result = sort( orders );

        Iterator<Node> iterator = result.getNodes().iterator();
        Assert.assertEquals( "node2", iterator.next().name().toString() );
        Assert.assertEquals( "node1", iterator.next().name().toString() );
        Assert.assertEquals( "node3", iterator.next().name().toString() );

    }

    @Test
    public void testByLongAndBooleanSorting()
    {
        String[] orders = {FIELD_LONG + " " + ORDER_DESC, FIELD_BOOL + " " + ORDER_DESC};
        FindNodesByQueryResult result = sort( orders );

        Iterator<Node> iterator = result.getNodes().iterator();
        Assert.assertEquals( "node2", iterator.next().name().toString() );
        Assert.assertEquals( "node3", iterator.next().name().toString() );
        Assert.assertEquals( "node1", iterator.next().name().toString() );

    }

    @Test
    public void testByBooleanAndLongSorting()
    {
        String[] orders = {FIELD_BOOL + " " + ORDER_DESC, FIELD_LONG + " " + ORDER_DESC};
        FindNodesByQueryResult result = sort( orders );

        Iterator<Node> iterator = result.getNodes().iterator();
        Assert.assertEquals( "node2", iterator.next().name().toString() );
        Assert.assertEquals( "node3", iterator.next().name().toString() );
        Assert.assertEquals( "node1", iterator.next().name().toString() );

    }

    @Test
    public void testByStringAndLongSorting()
    {
        String[] orders = {FIELD_STRING + " " + ORDER_DESC, FIELD_LONG + " " + ORDER_ASC};
        FindNodesByQueryResult result = sort( orders );

        Iterator<Node> iterator = result.getNodes().iterator();
        Assert.assertEquals( "node2", iterator.next().name().toString() );
        Assert.assertEquals( "node1", iterator.next().name().toString() );
        Assert.assertEquals( "node3", iterator.next().name().toString() );

    }

    @Test
    public void testByLongAndStringSorting()
    {
        String[] orders = {FIELD_LONG + " " + ORDER_ASC, FIELD_STRING + " " + ORDER_DESC};
        FindNodesByQueryResult result = sort( orders );

        Iterator<Node> iterator = result.getNodes().iterator();
        Assert.assertEquals( "node1", iterator.next().name().toString() );
        Assert.assertEquals( "node3", iterator.next().name().toString() );
        Assert.assertEquals( "node2", iterator.next().name().toString() );

    }

    @Test
    public void testByLongAndStringAndBooleanSorting()
    {
        String[] orders = {FIELD_LONG + " " + ORDER_DESC, FIELD_STRING + " " + ORDER_DESC, FIELD_BOOL + " " + ORDER_ASC};
        FindNodesByQueryResult result = sort( orders );

        Iterator<Node> iterator = result.getNodes().iterator();
        Assert.assertEquals( "node2", iterator.next().name().toString() );
        Assert.assertEquals( "node3", iterator.next().name().toString() );
        Assert.assertEquals( "node1", iterator.next().name().toString() );

    }

    @Test
    public void testByStringAndBooleanAndLongSorting()
    {
        String[] orders = {FIELD_STRING + " " + ORDER_DESC, FIELD_BOOL + " " + ORDER_DESC, FIELD_LONG + " " + ORDER_ASC};
        FindNodesByQueryResult result = sort( orders );

        Iterator<Node> iterator = result.getNodes().iterator();
        Assert.assertEquals( "node2", iterator.next().name().toString() );
        Assert.assertEquals( "node1", iterator.next().name().toString() );
        Assert.assertEquals( "node3", iterator.next().name().toString() );

    }


    private Map<String, Object> createPropertyMap( Long longValue, String stringValue, Boolean booleanValue )
    {
        Map<String, Object> properties = new HashMap();
        properties.put( FIELD_LONG, longValue );
        properties.put( FIELD_STRING, stringValue );
        properties.put( FIELD_BOOL, booleanValue );
        return properties;
    }

    private Node createNode( final String name, final Map<String, Object> properties, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        properties.keySet().stream().forEach( key -> {
            if ( FIELD_LONG.equals( key ) )
            {
                data.addLong( key, (Long) properties.get( key ) );
            }
            else if ( FIELD_BOOL.equals( key ) )
            {
                data.addBoolean( key, (Boolean) properties.get( key ) );
            }
            else if ( FIELD_STRING.equals( key ) )
            {
                data.addString( key, (String) properties.get( key ) );
            }
        } );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );
    }

    private FindNodesByQueryResult sort( String[] orders )
    {

        StringBuilder order = new StringBuilder();
        for ( String curOrder : orders )
        {
            order.append( curOrder ).append( "," );
        }
        order.deleteCharAt( order.length() - 1 );

        final List<OrderExpr> orderExpressions = QueryParser.parseOrderExpressions( order.toString() );
        final ConstraintExpr constraintExpr = QueryParser.parseCostraintExpression( PARENT_QUERY );
        final QueryExpr queryExpr = QueryExpr.from( constraintExpr, orderExpressions );

        final NodeQuery query = NodeQuery.create().
            query( queryExpr ).
            build();

        return doFindByQuery( query );
    }


}
