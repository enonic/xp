package com.enonic.xp.core.content;

import java.util.Iterator;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

import static org.junit.Assert.*;

public class ContentServiceImplTest_selectorSearch
    extends AbstractContentServiceTest
{

    @Test
    public void fulltext_order()
        throws Exception
    {
        final Content site1 = createContent( ContentPath.ROOT, "site1" );

        final Content third = createContent( site1.getPath(), "Fisk ost" );
        final Content second = createContent( site1.getPath(), "Fisk ost ost" );
        final Content first = createContent( site1.getPath(), "Fisk ost ost ost" );

        final FunctionExpr fulltext = FunctionExpr.from( "fulltext", ValueExpr.string( "displayName" ), ValueExpr.string( "ost" ) );
        final OrderExpr order = FieldOrderExpr.create( IndexPath.from( "_score" ), OrderExpr.Direction.DESC );

        final ContentQuery query = ContentQuery.create().
            queryExpr( QueryExpr.from( new DynamicConstraintExpr( fulltext ), order ) ).
            build();

        final FindContentByQueryResult result = contentService.find( FindContentByQueryParams.create().
            contentQuery( query ).
            build() );

        assertOrder( result, first, second, third );
    }

    @Ignore // Implement path search match first
    @Test
    public void same_site_first()
        throws Exception
    {
        final Content site1 = createContent( ContentPath.ROOT, "site1" );
        final Content site2 = createContent( ContentPath.ROOT, "site2" );

        final Content third = createContent( site1.getPath(), "Fisk ost" );
        final Content second = createContent( site2.getPath(), "Fisk ost" );

        final FunctionExpr fulltext = FunctionExpr.from( "fulltext", ValueExpr.string( "displayName" ), ValueExpr.string( "ost" ) );

        final OrderExpr order = FieldOrderExpr.create( IndexPath.from( "_score" ), OrderExpr.Direction.DESC );

        final ContentQuery query = ContentQuery.create().
            queryExpr( QueryExpr.from( new DynamicConstraintExpr( fulltext ), order ) ).
            build();

        final FindContentByQueryResult result = contentService.find( FindContentByQueryParams.create().
            contentQuery( query ).
            build() );

        assertOrder( result, second, third );
    }


    private void assertOrder( final FindContentByQueryResult result, final Content... expectedOrder )
    {
        assertEquals( "Expected [" + expectedOrder.length + "] number of hits in result", expectedOrder.length, result.getHits() );

        final Iterator<Content> iterator = result.getContents().iterator();

        for ( final Content content : expectedOrder )
        {
            assertTrue( "Expected more content, iterator empty", iterator.hasNext() );
            final Content next = iterator.next();
            assertEquals( "Expected content with path [" + content.getPath() + "] in this position, found [" + next.getPath() + "]",
                          content.getId(), next.getId() );
        }
    }


}
