package com.enonic.xp.core.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.parser.QueryParser;

import static org.assertj.core.api.Assertions.assertThat;

class FindNodesByQueryCommandTest_order
    extends AbstractNodeTest
{

    private static final String FIELD_LONG = "fieldLong";

    private static final String FIELD_BOOL = "fieldBoolean";

    private static final String FIELD_STRING = "fieldString";

    private static final String PARENT_QUERY = "_parentPath=\"/\"";

    private static final String ORDER_DESC = "DESC";

    private static final String ORDER_ASC = "ASC";

    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
        this.nodeInitializing();
    }

    private void nodeInitializing()
    {
        createNode( "node1", createPropertyMap( 1L, "b", false ), NodePath.ROOT );
        createNode( "node2", createPropertyMap( 3L, "c", true ), NodePath.ROOT );
        createNode( "node3", createPropertyMap( 2L, "a", false ), NodePath.ROOT );
        nodeService.refresh( RefreshMode.ALL );
    }

    @Test
    void testByLongSorting()
    {
        printContentRepoIndex();
        final FindNodesByQueryResult result = sort( FIELD_LONG + " " + ORDER_DESC );

        assertThat( result.getNodeIds() ).containsExactly( NodeId.from( "node2" ), NodeId.from( "node3" ), NodeId.from( "node1" ) );
    }

    @Test
    void testByStringSorting()
    {
        final FindNodesByQueryResult result = sort( FIELD_STRING + " " + ORDER_ASC );

        assertThat( result.getNodeIds() ).containsExactly( NodeId.from( "node3" ), NodeId.from( "node1" ), NodeId.from( "node2" ) );
    }

    @Test
    void testByBooleanSorting()
    {
        final FindNodesByQueryResult result = sort( FIELD_BOOL + " " + ORDER_DESC );

        assertThat( result.getNodeIds() ).containsExactly( NodeId.from( "node2" ), NodeId.from( "node1" ), NodeId.from( "node3" ) );
    }

    @Test
    void testByLongAndBooleanSorting()
    {
        final FindNodesByQueryResult result = sort( FIELD_LONG + " " + ORDER_DESC, FIELD_BOOL + " " + ORDER_DESC );

        assertThat( result.getNodeIds() ).containsExactly( NodeId.from( "node2" ), NodeId.from( "node3" ), NodeId.from( "node1" ) );
    }

    @Test
    void testByStringAndLongSorting()
    {
        final FindNodesByQueryResult result = sort( FIELD_STRING + " " + ORDER_DESC, FIELD_LONG + " " + ORDER_ASC );

        assertThat( result.getNodeIds() ).containsExactly( NodeId.from( "node2" ), NodeId.from( "node1" ), NodeId.from( "node3" ) );
    }

    @Test
    void testByLongAndStringSorting()
    {
        final FindNodesByQueryResult result = sort( FIELD_LONG + " " + ORDER_ASC, FIELD_STRING + " " + ORDER_DESC );

        assertThat( result.getNodeIds() ).containsExactly( NodeId.from( "node1" ), NodeId.from( "node3" ), NodeId.from( "node2" ) );
    }

    @Test
    void testByLongAndStringAndBooleanSorting()
    {
        final FindNodesByQueryResult result =
            sort( FIELD_LONG + " " + ORDER_DESC, FIELD_STRING + " " + ORDER_DESC, FIELD_BOOL + " " + ORDER_ASC );

        assertThat( result.getNodeIds() ).containsExactly( NodeId.from( "node2" ), NodeId.from( "node3" ), NodeId.from( "node1" ) );
    }

    @Test
    void testByStringAndBooleanAndLongSorting()
    {
        final FindNodesByQueryResult result =
            sort( FIELD_STRING + " " + ORDER_DESC, FIELD_BOOL + " " + ORDER_DESC, FIELD_LONG + " " + ORDER_ASC );

        assertThat( result.getNodeIds() ).containsExactly( NodeId.from( "node2" ), NodeId.from( "node1" ), NodeId.from( "node3" ) );
    }

    private Map<String, Object> createPropertyMap( Long longValue, String stringValue, Boolean booleanValue )
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put( FIELD_LONG, longValue );
        properties.put( FIELD_STRING, stringValue );
        properties.put( FIELD_BOOL, booleanValue );
        return properties;
    }

    private void createNode( final String name, final Map<String, Object> properties, final NodePath parent )
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

        createNode( CreateNodeParams.create().parent( parent ).setNodeId( NodeId.from( name ) ).name( name ).data( data ).build() );
    }

    private FindNodesByQueryResult sort( String... orders )
    {
        final List<OrderExpr> orderExpressions = QueryParser.parseOrderExpressions( String.join( ",", orders ) );
        final ConstraintExpr constraintExpr = QueryParser.parseCostraintExpression( PARENT_QUERY );
        final QueryExpr queryExpr = QueryExpr.from( constraintExpr, orderExpressions );

        final NodeQuery query = NodeQuery.create().query( queryExpr ).build();

        return doFindByQuery( query );
    }


}
