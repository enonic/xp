package com.enonic.xp.lib.content;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.aggregation.DateRangeBucket;
import com.enonic.xp.aggregation.NumericRangeBucket;
import com.enonic.xp.aggregation.SingleValueMetricAggregation;
import com.enonic.xp.aggregation.StatsAggregation;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.highlight.HighlightedProperty;
import com.enonic.xp.lib.content.mapper.ContentsResultMapper;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.sortvalues.SortValuesProperty;
import com.enonic.xp.testing.serializer.JsonMapGenerator;

class QueryContentHandlerTest
    extends BaseContentHandlerTest
{

    @Test
    void testExecute()
    {
        FindContentIdsByQueryResult queryResult = FindContentIdsByQueryResult.create()
            .contents( ContentIds.from( "contentId" ) )
            .sort( Collections.singletonMap( ContentId.from( "contentId" ), SortValuesProperty.create().values( 10 ).build() ) )
            .build();

        Contents contents = Contents.create()
            .add( Content.create().id( ContentId.from( "contentId" ) ).name( "name" ).parentPath( ContentPath.ROOT ).build() )
            .build();

        Mockito.when( contentService.find( Mockito.any( ContentQuery.class ) ) ).thenReturn( queryResult );
        Mockito.when( contentService.getByIds( Mockito.any( GetContentByIdsParams.class ) ) ).thenReturn( contents );

        QueryContentHandler instance = new QueryContentHandler();

        instance.initialize( newBeanContext( ResourceKey.from( "myapp:/test" ) ) );

        final ScriptValue sort = Mockito.mock( ScriptValue.class );
        final ScriptValue query = Mockito.mock( ScriptValue.class );

        Mockito.when( sort.getValue( String.class ) ).thenReturn( "getDistance(\"location\", \"83,80\", \"km\")" );
        Mockito.when( query.getValue( String.class ) ).thenReturn( "_name = \"cityName\"" );

        Mockito.when( query.isValue() ).thenReturn( true );
        Mockito.when( sort.isValue() ).thenReturn( true );

        instance.setSort( sort );
        instance.setQuery( query );

        JsonMapGenerator generator = new JsonMapGenerator();

        ContentsResultMapper resultMapper = (ContentsResultMapper) instance.execute();
        resultMapper.serialize( generator );

        final JsonNode actualJson = (JsonNode) generator.getRoot();

        Assertions.assertEquals( 1, actualJson.path( "count" ).asInt() );
        Assertions.assertTrue( actualJson.path( "hits" ).get( 0 ).path( "_sort" ).isArray() );
        Assertions.assertEquals( 10, actualJson.path( "hits" ).get( 0 ).path( "_sort" ).get( 0 ).asInt() );
    }

    @Test
    void testExample()
    {
        setupQuery( 2, true, true );
        runScript( "/lib/xp/examples/content/query.js" );
    }

    @Test
    void filterArray()
    {
        setupQuery( 2, false, false );
        runFunction( "/test/QueryContentHandlerTest_filter_array.js", "query" );
    }

    @Test
    void query()
    {
        setupQuery( 3, true, false );
        runFunction( "/test/QueryContentHandlerTest.js", "query" );
    }

    @Test
    void dslQuery()
    {
        setupQuery( 3, true, false );
        runFunction( "/test/QueryContentHandlerTest_dsl_query.js", "query" );
    }

    @Test
    void dslQueryInvalid()
    {
        setupQuery( 3, true, false );
        runFunction( "/test/QueryContentHandlerTest_dsl_query.js", "invalid" );
    }

    @Test
    void dslQueryEmpty()
    {
        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().contents( ContentIds.empty() ).aggregations( Aggregations.empty() ).build() );
        runFunction( "/test/QueryContentHandlerTest_dsl_query.js", "queryEmpty" );
    }

    @Test
    void dslSortSingle()
    {
        setupQuery( 2, false, false );
        runFunction( "/test/QueryContentHandlerTest_dsl_sort.js", "sortSingle" );
    }

    @Test
    void dslSortMultiple()
    {
        setupQuery( 2, false, false );
        runFunction( "/test/QueryContentHandlerTest_dsl_sort.js", "sortMultiple" );
    }

    @Test
    void dslSortEmpty()
    {
        setupQuery( 2, false, false );
        runFunction( "/test/QueryContentHandlerTest_dsl_sort.js", "sortEmpty" );
    }

    @Test
    void dslSortInvalid()
    {
        setupQuery( 2, false, false );
        runFunction( "/test/QueryContentHandlerTest_dsl_sort.js", "invalid" );
    }


    private void setupQuery( final int count, final boolean aggs, final boolean addHighlight )
    {
        final Contents contents = TestDataFixtures.newContents( count );

        final Instant t1 = Instant.parse( "2014-09-01T00:00:00.00Z" );
        final Instant t2 = Instant.parse( "2014-10-01T00:00:00.00Z" );
        final Instant t3 = Instant.parse( "2014-11-01T00:00:00.00Z" );

        final Buckets buckets1 = Buckets.create().
            add( Bucket.create().key( "male" ).docCount( 10 ).build() ).
            add( Bucket.create().key( "female" ).docCount( 12 ).build() ).
            build();
        final Buckets buckets2 = Buckets.create().
            add( Bucket.create().key( "2014-01" ).docCount( 8 ).build() ).
            add( Bucket.create().key( "2014-02" ).docCount( 10 ).build() ).
            add( Bucket.create().key( "2014-03" ).docCount( 12 ).build() ).
            build();
        final Buckets buckets3 = Buckets.create().
            add( NumericRangeBucket.create().key( "a" ).docCount( 2 ).to( 50 ).build() ).
            add( NumericRangeBucket.create().key( "b" ).docCount( 4 ).from( 50 ).to( 100 ).build() ).
            add( NumericRangeBucket.create().key( "c" ).docCount( 4 ).from( 100 ).build() ).
            build();
        final Buckets buckets4 = Buckets.create().
            add( DateRangeBucket.create().from( t1 ).docCount( 2 ).key( "date range bucket key" ).build() ).
            add( DateRangeBucket.create().to( t1 ).from( t2 ).docCount( 5 ).build() ).
            add( DateRangeBucket.create().to( t3 ).docCount( 7 ).build() ).
            build();
        final BucketAggregation aggr1 = BucketAggregation.bucketAggregation( "genders" ).buckets( buckets1 ).build();
        final BucketAggregation aggr2 = BucketAggregation.bucketAggregation( "by_month" ).buckets( buckets2 ).build();
        final BucketAggregation aggr3 = BucketAggregation.bucketAggregation( "price_ranges" ).buckets( buckets3 ).build();
        final BucketAggregation aggr4 = BucketAggregation.bucketAggregation( "my_date_range" ).buckets( buckets4 ).build();
        final StatsAggregation aggr5 = StatsAggregation.create( "item_count" ).avg( 3 ).max( 5 ).min( 1 ).sum( 15 ).count( 5 ).build();

        final Aggregations aggregations = Aggregations.from( aggr1, aggr2, aggr3, aggr4, aggr5 );

        final Map<ContentId, HighlightedProperties> highlight = Map.of( ContentId.from( "123" ), HighlightedProperties.create().
            add( HighlightedProperty.create().
                name( "property1" ).
                addFragment( "fragment1_1" ).
                addFragment( "fragment1_2" ).
                build() ).
            build(), ContentId.from( "456" ), HighlightedProperties.create().
            add( HighlightedProperty.create().
                name( "property2" ).
                addFragment( "fragment2_1" ).
                addFragment( "fragment2_2" ).
                build() ).
            build() );

        final FindContentIdsByQueryResult findResult = FindContentIdsByQueryResult.create().
            totalHits( 20 ).
            contents( contents.getIds() ).
            aggregations( aggs ? aggregations : null ).
            highlight( addHighlight ? highlight : null ).
            build();
        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findResult );
        Mockito.when( this.contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( contents );
    }

    @Test
    void queryEmpty()
    {
        final FindContentIdsByQueryResult findResult = FindContentIdsByQueryResult.create().
            totalHits( 0 ).
            contents( ContentIds.empty() ).
            aggregations( Aggregations.empty() ).
            highlight( Map.of() ).
            build();
        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findResult );
        Mockito.when( this.contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.empty() );

        runFunction( "/test/QueryContentHandlerTest.js", "queryEmpty" );
    }

    @Test
    void dslQueryExistsDslExpr()
    {
        setupQuery( 3, false, false );
        runFunction( "/test/QueryContentHandlerTest_dsl_query.js", "queryExistsDslExpr" );
    }

    @Test
    void testMinAggregation()
    {
        final SingleValueMetricAggregation minAgg = SingleValueMetricAggregation.create( "minPrice" ).value( 10.0 ).build();
        setUpForMetricsAggregations( minAgg );
        runFunction( "/test/QueryContentHandlerTest_MinAggregation.js", "queryWithAggregations" );
    }

    @Test
    void testMaxAggregation()
    {
        final SingleValueMetricAggregation maxAgg = SingleValueMetricAggregation.create( "maxPrice" ).value( 50.0 ).build();
        setUpForMetricsAggregations( maxAgg );
        runFunction( "/test/QueryContentHandlerTest_MaxAggregation.js", "queryWithAggregations" );
    }

    @Test
    void testValueCountAggregation()
    {
        final SingleValueMetricAggregation countAgg = SingleValueMetricAggregation.create( "countProductsWithPrice" ).value( 5 ).build();
        setUpForMetricsAggregations( countAgg );
        runFunction( "/test/QueryContentHandlerTest_ValueCountAggregation.js", "queryWithAggregations" );
    }

    private void setUpForMetricsAggregations( final SingleValueMetricAggregation aggregation )
    {
        final List<Content> toAddContents = new ArrayList<>();
        for ( int i = 1; i <= 5; i++ )
        {
            final PropertyTree data = new PropertyTree();
            data.addString( "category", "books" );
            data.addString( "productName", "product " + i );
            data.addDouble( "price", 10.0 * i );

            final Content content = Content.create().
                id( ContentId.from( "id" + i ) ).
                name( "name" + i ).
                displayName( "My Content " + i ).
                parentPath( ContentPath.from( "/a/b" ) ).
                modifier( PrincipalKey.from( "user:system:admin" ) ).
                modifiedTime( Instant.ofEpochSecond( 0 ) ).
                creator( PrincipalKey.from( "user:system:admin" ) ).
                createdTime( Instant.ofEpochSecond( 0 ) ).
                data( data ).
                build();

            toAddContents.add( content );
        }

        final Buckets bucket = Buckets.create().
            add( Bucket.create().
                key( "books" ).
                docCount( 5 ).
                addAggregations( Aggregations.from( aggregation ) ).
                build() ).build();

        final Contents contents = Contents.from( toAddContents );

        final FindContentIdsByQueryResult findResult = FindContentIdsByQueryResult.create().
            totalHits( 5 ).
            contents( contents.getIds() ).
            aggregations( Aggregations.from( BucketAggregation.bucketAggregation( "products" ).buckets( bucket ).build() ) ).
            build();

        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( findResult );
        Mockito.when( this.contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( contents );
    }
}
