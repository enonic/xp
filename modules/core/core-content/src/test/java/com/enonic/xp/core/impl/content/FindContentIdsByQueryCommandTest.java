package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.highlight.HighlightedProperty;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.sortvalues.SortValuesProperty;

public class FindContentIdsByQueryCommandTest
{

    private NodeService nodeService;

    private ContentNodeTranslator translator;

    private ContentTypeService contentTypeService;

    private EventPublisher eventPublisher;

    @BeforeEach
    public void setUp()
    {
        nodeService = Mockito.mock( NodeService.class );
        translator = Mockito.mock( ContentNodeTranslator.class );
        contentTypeService = Mockito.mock( ContentTypeService.class );
        eventPublisher = Mockito.mock( EventPublisher.class );
    }

    @Test
    public void test()
    {
        FindNodesByQueryResult nodesByQueryResult = FindNodesByQueryResult.create().
            addNodeHit( NodeHit.create().
                nodeId( NodeId.from( "nodeId" ) ).
                score( 1.0f ).
                sort( SortValuesProperty.create().
                    values( 84 ).
                    build() ).
                highlight( HighlightedProperties.create().
                    add( HighlightedProperty.create().
                        name( "name" ).
                        addFragment( "fragment" ).
                        build() ).
                    build() ).
                build() ).
            build();

        Mockito.when( nodeService.findByQuery( Mockito.any( NodeQuery.class ) ) ).thenReturn( nodesByQueryResult );

        FindContentIdsByQueryCommand command = FindContentIdsByQueryCommand.create().
            translator( translator ).
            nodeService( nodeService ).
            contentTypeService( contentTypeService ).
            eventPublisher( eventPublisher ).
            query( ContentQuery.create().
                queryExpr( QueryExpr.from( null, new DynamicOrderExpr(
                    FunctionExpr.from( "geoDistance", ValueExpr.string( "my-value" ), ValueExpr.geoPoint( "83,80" ),
                                       ValueExpr.string( "km" ) ), OrderExpr.Direction.ASC ) ) ).
                build() ).
            build();

        FindContentIdsByQueryResult result = command.execute();

        Assertions.assertFalse( result.getSort().isEmpty() );
        Assertions.assertEquals( 1, result.getSort().size() );
        Assertions.assertEquals( 1, result.getScore().size() );
    }

}
