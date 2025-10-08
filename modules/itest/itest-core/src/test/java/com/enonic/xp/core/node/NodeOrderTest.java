package com.enonic.xp.core.node;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.util.GeoPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeOrderTest
    extends AbstractNodeTest
{

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void geo_distance_sorting()
        throws Exception
    {
        final Node node1 = createNode( "node1", ValueFactory.newGeoPoint( GeoPoint.from( "80,80" ) ) );
        final Node node2 = createNode( "node2", ValueFactory.newGeoPoint( GeoPoint.from( "81,80" ) ) );
        final Node node3 = createNode( "node3", ValueFactory.newGeoPoint( GeoPoint.from( "82,80" ) ) );
        final Node node4 = createNode( "node4", ValueFactory.newGeoPoint( GeoPoint.from( "83,80" ) ) );
        nodeService.refresh( RefreshMode.ALL );

        final NodeQuery distanceQuery = NodeQuery.create().
            query( QueryExpr.from( null, new DynamicOrderExpr(
                FunctionExpr.from( "geoDistance", ValueExpr.string( "my-value" ), ValueExpr.geoPoint( "83,80" ) ),
                OrderExpr.Direction.ASC ) ) ).
            build();

        final SearchResult result = searchService.query( distanceQuery, SingleRepoSearchSource.from( ContextAccessor.current() ) );

        final Iterator<String> iterator = result.getIds().iterator();
        assertEquals( node4.id(), NodeId.from( iterator.next() ) );
        assertEquals( node3.id(), NodeId.from( iterator.next() ) );
        assertEquals( node2.id(), NodeId.from( iterator.next() ) );
        assertEquals( node1.id(), NodeId.from( iterator.next() ) );

    }

    @Test
    public void score_sorting()
        throws Exception
    {
        final Node node1 = createNode( "node1", ValueFactory.newString( "denne har en fisk" ) );
        final Node node2 = createNode( "node2", ValueFactory.newString( "denne har fisk og ost" ) );
        final Node node3 = createNode( "node3", ValueFactory.newString( "mens denne har både fisk, ost og pølse" ) );
        final Node node4 =
            createNode( "node4", ValueFactory.newString( "denne vinner, siden den har alle sammen: fisk, ost, pølse og pai" ) );
        nodeService.refresh( RefreshMode.ALL );

        final FunctionExpr fulltextExpression =
            FunctionExpr.from( "fulltext", ValueExpr.string( "my-value" ), ValueExpr.string( "pai fisk pølse ost" ),
                               ValueExpr.string( "OR" ) );

        final NodeQuery fulltextQuery = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr( fulltextExpression ),
                                   FieldOrderExpr.create( IndexPath.from( "_score" ), OrderExpr.Direction.DESC ) ) ).
            build();

        printContentRepoIndex();

        final SearchResult result = searchService.query( fulltextQuery, SingleRepoSearchSource.from( ContextAccessor.current() ) );

        assertEquals( 4, result.getHits().size() );

        final Iterator<String> iterator = result.getIds().iterator();
        assertEquals( node4.id(), NodeId.from( iterator.next() ) );
        assertEquals( node3.id(), NodeId.from( iterator.next() ) );
        assertEquals( node2.id(), NodeId.from( iterator.next() ) );
        assertEquals( node1.id(), NodeId.from( iterator.next() ) );
    }

    private Node createNode( final String name, final Value value )
    {
        final PropertyTree data = new PropertyTree();
        data.setProperty( "my-value", value );

        return createNode( CreateNodeParams.create().
            name( name ).
            parent( NodePath.ROOT ).
            data( data ).
            setNodeId( NodeId.from( name ) ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                build() ).
            build() );
    }

}
