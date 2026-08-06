package com.enonic.xp.core.impl.content;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.context.ContextAccessorSupport;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentQueryNodeQueryTranslatorTest
{
    @BeforeEach
    void setUp()
    {
        ContextAccessorSupport.getInstance()
            .set( ContextBuilder.create()
                      .repositoryId( "com.enonic.cms.default" )
                      .branch( ContentConstants.BRANCH_DRAFT )
                      .build() );
    }

    @AfterEach
    void tearDown()
    {
        ContextAccessorSupport.getInstance().remove();
    }

    @Test
    void translate_without_parent()
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( ContentQuery.create().build() ).build();

        assertNull( nodeQuery.getParent() );
        assertTrue( nodeQuery.getQuery().getOrderList().isEmpty() );
    }

    @Test
    void translate_parent()
    {
        final ContentQueryParent parent = new ContentQueryParent( new NodePath( "/content/mysite/articles" ), null );

        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( ContentQuery.create().build(), parent ).build();

        assertEquals( new NodePath( "/content/mysite/articles" ), nodeQuery.getParent() );
    }

    @Test
    void translate_child_order_of_parent()
    {
        final ContentQueryParent parent =
            new ContentQueryParent( new NodePath( "/content/mysite" ), ChildOrder.from( "_manualordervalue DESC" ) );

        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( ContentQuery.create().build(), parent ).build();

        assertEquals( List.copyOf( ChildOrder.from( "_manualordervalue DESC" ).getOrderExpressions().getList() ),
                      nodeQuery.getQuery().getOrderList() );
    }

    @Test
    void translate_keeps_own_order_over_child_order_of_parent()
    {
        // a parent resolved without a child order is how the resolver signals that the query brings its own ordering
        final ContentQueryParent parent = new ContentQueryParent( new NodePath( "/content/mysite" ), null );

        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate(
            ContentQuery.create().queryExpr( QueryParser.parse( "order by _path asc" ) ).build(), parent ).build();

        final List<OrderExpr> orderList = nodeQuery.getQuery().getOrderList();
        assertEquals( 1, orderList.size() );
        assertEquals( "_path", ( (FieldOrderExpr) orderList.get( 0 ) ).getField().getFieldPath() );
        assertEquals( OrderExpr.Direction.ASC, ( (FieldOrderExpr) orderList.get( 0 ) ).getDirection() );
    }
}
