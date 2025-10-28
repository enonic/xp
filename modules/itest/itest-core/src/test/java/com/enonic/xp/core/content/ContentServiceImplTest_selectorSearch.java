package com.enonic.xp.core.content;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;

class ContentServiceImplTest_selectorSearch
    extends AbstractContentServiceTest
{

    @Test
    void fulltext_order()
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

        final ContentIds result = contentService.find( query ).getContentIds();

        assertOrder( result, first, second, third );
    }

    @Disabled // Implement path search match first
    @Test
    void same_site_first()
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

        final ContentIds result = contentService.find( query ).getContentIds();

        assertOrder( result, second, third );
    }


}
