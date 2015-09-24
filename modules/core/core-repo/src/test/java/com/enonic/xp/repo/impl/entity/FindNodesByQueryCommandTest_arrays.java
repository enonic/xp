package com.enonic.xp.repo.impl.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

import static org.junit.Assert.*;

public class FindNodesByQueryCommandTest_arrays
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
    public void nested_array_elements()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet cars = data.addSet( "cars" );
        final PropertySet car1 = cars.addSet( "car" );
        car1.setString( "brand", "skoda" );
        car1.setString( "color", "artic grey" );

        final PropertySet car2 = cars.addSet( "car" );
        car2.setString( "brand", "volvo" );
        car2.setString( "color", "red" );

        final Node node1 = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        printContentRepoIndex();

        compareQueryAndAssert( "cars.car.brand", "skoda", node1.id() );
        compareQueryAndAssert( "cars.car.brand", "volvo", node1.id() );
        compareQueryAndAssert( "cars.car.color", "red", node1.id() );
        compareQueryAndAssert( "cars.car.color", "artic grey", node1.id() );
    }

    private void compareQueryAndAssert( final String path1, final String value1, final NodeId nodeId )
    {
        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.eq( FieldExpr.from( path1 ), ValueExpr.string( value1 ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( nodeId ) );
    }


}
