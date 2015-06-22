package com.enonic.xp.content.query;

import java.util.Arrays;

import org.junit.Test;

import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.data.Value;
import com.enonic.xp.query.aggregation.GeoDistanceAggregationQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.util.GeoPoint;

import static org.junit.Assert.*;

public class ContentQueryTest
{
    @Test
    public void testBuilder()
    {
        final ContentQuery query = createTestQuery();

        assertNotNull( query );
        assertEquals( 10, query.getFrom() );
        assertEquals( 10, query.getSize() );
        assertEquals( 3, query.getAggregationQueries().getSize() );
        assertEquals( 3, query.getContentTypes().getSize() );
        assertNotNull( query.getQueryExpr() );
        assertEquals( 1, query.getQueryFilters().getSize() );
    }

    private ContentQuery createTestQuery()
    {
        final GeoDistanceAggregationQuery query1 = GeoDistanceAggregationQuery.create( "geo" ).
            unit( "inch" ).
            origin( GeoPoint.from( "20,30" ) ).
            build();

        final GeoDistanceAggregationQuery query2 = GeoDistanceAggregationQuery.create( "geo" ).
            unit( "inch" ).
            origin( GeoPoint.from( "20,30" ) ).
            build();
        final GeoDistanceAggregationQuery query3 = GeoDistanceAggregationQuery.create( "geo" ).
            unit( "inch" ).
            origin( GeoPoint.from( "20,30" ) ).
            build();

        final ContentQuery.Builder builder = ContentQuery.newContentQuery();
        builder.addContentTypeName( ContentTypeName.imageMedia() );
        builder.addContentTypeNames( ContentTypeNames.from( ContentTypeName.archiveMedia(), ContentTypeName.dataMedia() ) );
        builder.aggregationQuery( query1 );
        builder.aggregationQueries( Arrays.asList( query2, query3 ) );
        builder.from( 10 );
        builder.size( 10 );
        builder.queryExpr( QueryExpr.from( CompareExpr.eq( FieldExpr.from( "name" ), ValueExpr.string( "testerson" ) ) ) );
        builder.queryFilter( RangeFilter.create().from( Value.newDouble( 2.0 ) ).to( Value.newDouble( 10.0 ) ).build() );

        return builder.build();
    }

}
