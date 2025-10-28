package com.enonic.xp.content.query;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.aggregation.GeoDistanceAggregationQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.query.highlight.HighlightQueryProperty;
import com.enonic.xp.query.highlight.HighlightQuerySettings;
import com.enonic.xp.query.highlight.constants.Encoder;
import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;
import com.enonic.xp.query.highlight.constants.TagsSchema;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.util.GeoPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContentQueryTest
{
    @Test
    void testBuilder()
    {
        final ContentQuery query = createTestQuery();

        assertNotNull( query );
        assertEquals( 10, query.getFrom() );
        assertEquals( 10, query.getSize() );
        assertEquals( 3, query.getAggregationQueries().getSize() );
        assertEquals( 3, query.getContentTypes().getSize() );
        assertNotNull( query.getQueryExpr() );
        assertEquals( 1, query.getQueryFilters().getSize() );

        assertNotNull( query.getHighlight() );
        assertEquals( Encoder.HTML, query.getHighlight().getSettings().getEncoder() );
        assertEquals( Fragmenter.SIMPLE, query.getHighlight().getSettings().getFragmenter() );
        assertEquals( 1, (int) query.getHighlight().getSettings().getFragmentSize() );
        assertEquals( 2, (int) query.getHighlight().getSettings().getNoMatchSize() );
        assertEquals( 3, (int) query.getHighlight().getSettings().getNumOfFragments() );
        assertEquals( Order.SCORE, query.getHighlight().getSettings().getOrder() );
        assertEquals( List.of( "<a>", "<b>" ), query.getHighlight().getSettings().getPreTags() );
        assertEquals( List.of( "<c>", "<d>" ), query.getHighlight().getSettings().getPostTags() );
        assertEquals( true, query.getHighlight().getSettings().getRequireFieldMatch() );
        assertEquals( TagsSchema.STYLED, query.getHighlight().getSettings().getTagsSchema() );

    }

    private ContentQuery createTestQuery()
    {
        final GeoDistanceAggregationQuery query1 =
            GeoDistanceAggregationQuery.create( "geo" ).unit( "inch" ).origin( GeoPoint.from( "20,30" ) ).build();

        final GeoDistanceAggregationQuery query2 =
            GeoDistanceAggregationQuery.create( "geo" ).unit( "inch" ).origin( GeoPoint.from( "20,30" ) ).build();
        final GeoDistanceAggregationQuery query3 =
            GeoDistanceAggregationQuery.create( "geo" ).unit( "inch" ).origin( GeoPoint.from( "20,30" ) ).build();

        final HighlightQuery highlightQuery = HighlightQuery.create()
            .property( HighlightQueryProperty.create( "propertyToHighlight" ).build() )
            .settings( HighlightQuerySettings.create()
                           .encoder( Encoder.HTML )
                           .fragmenter( Fragmenter.SIMPLE )
                           .fragmentSize( 1 )
                           .noMatchSize( 2 )
                           .numOfFragments( 3 )
                           .order( Order.SCORE )
                           .addPreTags( List.of( "<a>", "<b>" ) )
                           .addPostTags( List.of( "<c>", "<d>" ) )
                           .requireFieldMatch( true )
                           .tagsSchema( TagsSchema.STYLED )
                           .build() )
            .build();

        final ContentQuery.Builder builder = ContentQuery.create();
        builder.addContentTypeName( ContentTypeName.imageMedia() );
        builder.addContentTypeNames( ContentTypeNames.from( ContentTypeName.archiveMedia(), ContentTypeName.dataMedia() ) );
        builder.aggregationQuery( query1 );
        builder.aggregationQueries( Arrays.asList( query2, query3 ) );
        builder.highlight( highlightQuery );
        builder.from( 10 );
        builder.size( 10 );
        builder.queryExpr( QueryExpr.from( CompareExpr.eq( FieldExpr.from( "name" ), ValueExpr.string( "testerson" ) ) ) );
        builder.queryFilter( RangeFilter.create()
                                 .from( ValueFactory.newDouble( 2.0 ) )
                                 .to( ValueFactory.newDouble( 10.0 ) )
                                 .fieldName( "fieldName" )
                                 .build() );

        return builder.build();
    }

}
