package com.enonic.xp.core.impl.content;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextAccessorSupport;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        assertNull( nodeQuery.getQuery().getConstraint() );
        assertTrue( nodeQuery.getQuery().getOrderList().isEmpty() );
        assertContentRootFilter( nodeQuery, "/content" );
    }

    @Test
    void translate_parent()
    {
        final ContentQueryParent parent = new ContentQueryParent( new NodePath( "/content/mysite/articles" ), null );

        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( ContentQuery.create().build(), parent ).build();

        assertEquals( new NodePath( "/content/mysite/articles" ), nodeQuery.getParent() );
        assertNull( nodeQuery.getQuery().getConstraint() );
        assertContentRootFilter( nodeQuery, "/content" );
    }

    @Test
    void translate_recursive_parent()
    {
        final ContentQueryParent parent = new ContentQueryParent( new NodePath( "/content/mysite/articles" ), null );

        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate(
            ContentQuery.create().parentPath( ContentPath.from( "/mysite/articles" ) ).recursive( true ).build(), parent ).build();

        // matched by a path prefix instead of the _parentPath term filter, so descendants at any depth are included
        assertNull( nodeQuery.getParent() );
        assertEquals( "_path LIKE '/content/mysite/articles/*'", nodeQuery.getQuery().getConstraint().toString() );
        assertContentRootFilter( nodeQuery, "/content" );
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
    void resolve_recursive_takes_no_child_order_from_the_parent()
    {
        // a parent orders its own children only, so a recursive query wants none of it - and the resolver then never reads the parent,
        // which is why a null command suffices here
        final ContentQueryParent parent = ContentQueryParent.resolve(
            ContentQuery.create().parentPath( ContentPath.from( "/mysite" ) ).recursive( true ).build(), null );

        assertNotNull( parent );
        assertNull( parent.childOrder() );
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

    @Test
    void contentRootFilterFollowsContextOfEachQuery()
    {
        final NodeQuery archived = ContextBuilder.from( ContextAccessor.current() )
            .attribute( ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE, ArchiveConstants.ARCHIVE_ROOT_PATH )
            .build()
            .callWith( () -> ContentQueryNodeQueryTranslator.translate( ContentQuery.create().build() ).build() );
        final NodeQuery content = ContentQueryNodeQueryTranslator.translate( ContentQuery.create().build() ).build();

        assertContentRootFilter( archived, "/archive" );
        assertContentRootFilter( content, "/content" );
    }

    private static void assertContentRootFilter( final NodeQuery nodeQuery, final String root )
    {
        final BooleanFilter filter = nodeQuery.getQueryFilters()
            .stream()
            .filter( BooleanFilter.class::isInstance )
            .map( BooleanFilter.class::cast )
            .findFirst()
            .orElseThrow();
        final ValueFilter hierarchy = (ValueFilter) filter.getMust().get( 0 );
        assertEquals( "_path._path", hierarchy.getFieldName() );
        assertEquals( List.of( root ), hierarchy.getValues().stream().map( Value::asString ).toList() );
        final ValueFilter contentRoot = (ValueFilter) filter.getMustNot().get( 0 );
        assertEquals( "_path", contentRoot.getFieldName() );
        assertEquals( List.of( root ), contentRoot.getValues().stream().map( Value::asString ).toList() );
    }
}
