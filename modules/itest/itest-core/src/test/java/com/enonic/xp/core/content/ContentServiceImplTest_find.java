package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.Buckets;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.util.GeoPoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentServiceImplTest_find
    extends AbstractContentServiceTest
{

    @Test
    void order_by_path()
    {
        final Content site = createContent( ContentPath.ROOT, "a" );

        final Content child3 = createContent( site.getPath(), "d" );
        final Content child2 = createContent( site.getPath(), "c" );
        final Content child1 = createContent( site.getPath(), "b" );

        final ContentQuery queryOrderAsc = ContentQuery.create().queryExpr( QueryParser.parse( "order by _path asc" ) ).build();

        assertOrder( contentService.find( queryOrderAsc ).getContentIds(), site, child1, child2, child3 );

        final ContentQuery queryOrderDesc = ContentQuery.create().queryExpr( QueryParser.parse( "order by _path desc" ) ).build();

        assertOrder( contentService.find( queryOrderDesc ).getContentIds(), child3, child2, child1, site );
    }

    @Test
    void test_pending_publish_master()
    {
        ctxMaster().callWith( () -> {
            final Content content = createAndPublishContent( ContentPath.ROOT, Instant.now().plus( Duration.ofDays( 1 ) ) );

            final FindContentIdsByQueryResult result = findContent( content.getId() );
            assertEquals( 0, result.getTotalHits() );
            return null;
        } );
    }

    @Test
    void test_publish_expired_master()
    {
        ctxMaster().callWith( () -> {
            final Content content = createAndPublishContent( ContentPath.ROOT, Instant.now().minus( Duration.ofDays( 2 ) ) , Instant.now().minus( Duration.ofDays( 1 ) ) );

            final FindContentIdsByQueryResult result = findContent( content.getId() );
            printAllIndexContent( IndexNameResolver.resolveSearchIndexName( testprojectName.getRepoId()  ), "draft"  );
            assertEquals( 0, result.getTotalHits() );
            return null;
        } );
    }

    @Test
    void test_published_master()
    {
        ctxMaster().callWith( () -> {
            final Content content = createAndPublishContent( ContentPath.ROOT, Instant.now().minus( Duration.ofDays( 1 ) ),
                                                             Instant.now().plus( Duration.ofDays( 1 ) ));

            final FindContentIdsByQueryResult result = findContent( content.getId() );

            assertEquals( 1, result.getTotalHits() );
            return null;
        } );
    }

    @Test
    void multipleFilters()
    {
        createContent( ContentPath.ROOT, "title1" );
        createContent( ContentPath.ROOT, "title2" );

        FindContentIdsByQueryResult result = this.contentService.find( ContentQuery.create()
                                                                           .queryFilter( ValueFilter.create()
                                                                                             .fieldName( ContentPropertyNames.DISPLAY_NAME )
                                                                                             .addValue( ValueFactory.newString( "title1" ) )
                                                                                             .build() )
                                                                           .queryFilter( ValueFilter.create()
                                                                                             .fieldName( ContentPropertyNames.DISPLAY_NAME )
                                                                                             .addValue( ValueFactory.newString( "title2" ) )
                                                                                             .build() )
                                                                           .build() );

        // Filters will be "must", and no entry matches both titles
        assertEquals( 0, result.getContentIds().getSize() );
    }

    @Test
    void aggregations()
    {
        createContent( ContentPath.ROOT, "title1",new PropertyTree(), ContentTypeName.folder() );
        createContent( ContentPath.ROOT, "title2",new PropertyTree(), ContentTypeName.unknownMedia() );
        createContent( ContentPath.ROOT, "title3",new PropertyTree(), ContentTypeName.unknownMedia() );

        FindContentIdsByQueryResult result = this.contentService.find( ContentQuery.create()
                                                                           .aggregationQuery( TermsAggregationQuery.create( "type" ).
                                                                               fieldName( "type" ).
                                                                               orderDirection( TermsAggregationQuery.Direction.DESC ).
                                                                               build() )
                                                                           .size( 0 )
                                                                           .build() );

        final Buckets buckets = ( (BucketAggregation) result.getAggregations().get( "type" ) ).getBuckets();

        assertThat( buckets ).extracting( "key", "docCount" ).containsExactly( tuple( "media:unknown", 2L ), tuple( "base:folder", 1L ) );
        assertEquals( result.getTotalHits(), 3 );
        assertEquals( result.getContentIds().getSize(), 0 );
    }

    @Test
    void aggregations_with_unlimited_size()
    {
        final Content content1 = createContent( ContentPath.ROOT, "title1", new PropertyTree(), ContentTypeName.folder() );
        final Content content2 = createContent( ContentPath.ROOT, "title2", new PropertyTree(), ContentTypeName.unknownMedia() );
        final Content content3 = createContent( ContentPath.ROOT, "title3", new PropertyTree(), ContentTypeName.unknownMedia() );

        FindContentIdsByQueryResult result = this.contentService.find( ContentQuery.create()
                                                                           .aggregationQuery( TermsAggregationQuery.create( "type" )
                                                                                                  .fieldName( "type" )
                                                                                                  .orderDirection(
                                                                                                      TermsAggregationQuery.Direction.DESC )
                                                                                                  .build() )
                                                                           .size( -1 )
                                                                           .build() );

        final Buckets buckets = ( (BucketAggregation) result.getAggregations().get( "type" ) ).getBuckets();

        assertThat( buckets ).extracting( "key", "docCount" ).containsExactly( tuple( "media:unknown", 2L ), tuple( "base:folder", 1L ) );
        assertThat( result.getContentIds() ).containsExactlyInAnyOrder( content1.getId(), content2.getId(), content3.getId() );
    }

    @Test
    void dsl_query_geo_point()
    {
        PropertyTree data = createPropertyTreeForAllInputTypes();

        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .type( ContentTypeName.folder() )
                                                                .contentData( data )
                                                                .name( "myContent" )
                                                                .parent( ContentPath.ROOT )
                                                                .displayName( "my display-name" )
                                                                .build() );

        final Content child2 = createContent( ContentPath.ROOT, "c" );

        final PropertyTree request = new PropertyTree();
        final PropertySet fulltext = request.addSet( "term" );

        fulltext.addString( "field", "data.geoPoint" );
        fulltext.addString( "value", "59.91273,10.74609" );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        assertEquals( 1, contentService.find( queryDsl ).getContentIds().getSize() );
    }

    @Test
    void dsl_query_term_uppercase()
    {
        PropertyTree data = createPropertyTreeForAllInputTypes();

        this.contentService.create( CreateContentParams.create()
                                        .type( ContentTypeName.folder() )
                                        .contentData( data )
                                        .name( "myContent1" )
                                        .parent( ContentPath.ROOT )
                                        .displayName( "My DisplayName" )
                                        .build() );

        final PropertyTree request = new PropertyTree();

        final PropertySet term = request.addSet( "term" );

        term.addString( "field", "displayName" );
        term.addString( "value", "My DisplayName" );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        assertEquals( 1, contentService.find( queryDsl ).getContentIds().getSize() );
    }

    @Test
    void dsl_query_term_localdatetime()
    {
        PropertyTree data = createPropertyTreeForAllInputTypes();

        this.contentService.create( CreateContentParams.create()
                                        .type( ContentTypeName.folder() )
                                        .contentData( data )
                                        .name( "myContent1" )
                                        .parent( ContentPath.ROOT )
                                        .displayName( "My DisplayName" )
                                        .build() );

        final PropertyTree request = new PropertyTree();

        final PropertySet term = request.addSet( "term" );

        term.addString( "field", "data.localDateTime" );
        term.addLocalDateTime( "value", LocalDateTime.of( 2015, 3, 13, 10, 0, 0 ) );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        assertEquals( 1, contentService.find( queryDsl ).getContentIds().getSize() );
    }

    @Test
    void dsl_query_term_localdate()
    {
        PropertyTree data = createPropertyTreeForAllInputTypes();

        this.contentService.create( CreateContentParams.create()
                                        .type( ContentTypeName.folder() )
                                        .contentData( data )
                                        .name( "myContent1" )
                                        .parent( ContentPath.ROOT )
                                        .displayName( "My DisplayName" )
                                        .build() );

        final PropertyTree request = new PropertyTree();

        final PropertySet term = request.addSet( "term" );

        term.addString( "field", "data.date" );
        term.addLocalDate( "value", LocalDate.of( 2015, 3, 13 ) );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        assertEquals( 1, contentService.find( queryDsl ).getContentIds().getSize() );
    }

    @Test
    void dsl_query_term_localdatetime_string()
    {
        PropertyTree data = createPropertyTreeForAllInputTypes();

        this.contentService.create( CreateContentParams.create()
                                        .type( ContentTypeName.folder() )
                                        .contentData( data )
                                        .name( "myContent1" )
                                        .parent( ContentPath.ROOT )
                                        .displayName( "My DisplayName" )
                                        .build() );

        final PropertyTree request = new PropertyTree();

        final PropertySet term = request.addSet( "term" );

        term.addString( "field", "data.localDateTime" );
        term.addString( "value", "2015-03-13T10:00:00" );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        assertEquals( 1, contentService.find( queryDsl ).getContentIds().getSize() );
    }

    @Test
    void dsl_query_term_geopoint()
    {
        PropertyTree data = createPropertyTreeForAllInputTypes();

        this.contentService.create( CreateContentParams.create()
                                        .type( ContentTypeName.folder() )
                                        .contentData( data )
                                        .name( "myContent1" )
                                        .parent( ContentPath.ROOT )
                                        .displayName( "My DisplayName" )
                                        .build() );

        final PropertyTree request = new PropertyTree();

        final PropertySet fulltext = request.addSet( "term" );

        fulltext.addString( "field", "data.geoPoint" );
        fulltext.addGeoPoint( "value", GeoPoint.from( "59.9127300, 10.7460900" ) );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        assertEquals( 1, contentService.find( queryDsl ).getContentIds().getSize() );
    }

    @Test
    void dsl_like_query()
    {
        final Content site = createContent( ContentPath.ROOT, "a" );

        final Content child3 = createContent( site.getPath(), "d" );
        final Content child2 = createContent( site.getPath(), "c" );
        final Content child1 = createContent( site.getPath(), "b" );

        final PropertyTree request = new PropertyTree();
        final PropertySet fulltext = request.newSet();
        fulltext.addString( "field", "displayName" );
        fulltext.addString( "value", "*d" );

        request.addSet( "like", fulltext );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        final FindContentIdsByQueryResult result = contentService.find( queryDsl );

        assertOrder( result.getContentIds(), child3 );
    }

    @Test
    void dsl_query()
    {
        final Content site = createContent( ContentPath.ROOT, "a" );

        final Content child3 = createContent( site.getPath(), "d" );
        final Content child2 = createContent( site.getPath(), "c" );
        final Content child1 = createContent( site.getPath(), "b" );

        final PropertyTree request = new PropertyTree();
        final PropertySet fulltext = request.newSet();
        fulltext.addStrings( "fields", "displayName" );
        fulltext.addString( "query", "c" );

        request.addSet( "fulltext", fulltext );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        assertOrder( contentService.find( queryDsl ).getContentIds(), child2 );
    }

    @Test
    void dsl_exists_string()
    {
        PropertyTree siteData = new PropertyTree();
        siteData.setString( "myField", "stringValue" );
        final Content site = createContent( ContentPath.ROOT, "a", siteData );

        PropertyTree cData = new PropertyTree();
        cData.setString( "myField", null );
        final Content child1 = createContent( site.getPath(), "c", cData );

        PropertyTree dData = new PropertyTree();
        dData.setString( "myField", "" );
        final Content child2 = createContent( site.getPath(), "b", dData );

        testExists( site.getId() );
        // child2 with empty string was not found by request because of legacy behavior in indexing, which removes such strings
    }

    @Test
    void dsl_exists_long()
    {
        PropertyTree data = new PropertyTree();
        data.setLong( "myField", 2L );
        final Content content1 = createContent( ContentPath.ROOT, "displayName", data );

        data = new PropertyTree();
        data.setLong( "myField", null );
        final Content content2 = createContent( ContentPath.ROOT, "a", data );

        data = new PropertyTree();
        data.setLong( "myField", 0L );
        final Content content3 = createContent( ContentPath.ROOT, "b", data );

        testExists( content1.getId(), content3.getId() );
    }

    @Test
    void dsl_exists_boolean()
    {
        PropertyTree data = new PropertyTree();
        data.setBoolean( "myField", true );
        final Content content1 = createContent( ContentPath.ROOT, "displayName", data );

        data = new PropertyTree();
        data.setBoolean( "myField", false );
        final Content content2 = createContent( ContentPath.ROOT, "a", data );

        data = new PropertyTree();
        data.setBoolean( "myField", null );
        final Content content3 = createContent( ContentPath.ROOT, "b", data );

        testExists( content1.getId(), content2.getId() );
    }

    @Test
    void dsl_exists_set()
    {
        PropertyTree data = new PropertyTree();
        data.setSet( "myField", data.newSet() );
        final Content content1 = createContent( ContentPath.ROOT, "displayName", data );

        data = new PropertyTree();
        PropertySet set = data.newSet();
        set.addString( "sd", "sd" );

        data.setSet( "myField", set );
        final Content content2 = createContent( ContentPath.ROOT, "a", data );

        testExists( content2.getId() );
    }

    private void testExists( final ContentId... existedContents )
    {
        final PropertyTree request = new PropertyTree();
        final PropertySet exists = request.newSet();
        exists.addStrings( "field", "data.myField" );

        request.addSet( "exists", exists );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        assertThat( contentService.find( queryDsl ).getContentIds() ).containsExactlyInAnyOrder( existedContents );
    }

    @Test
    void dsl_query_empty()
    {
        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( new PropertyTree() ) ) ).build();

        assertThrows( IllegalArgumentException.class, () -> contentService.find( queryDsl ) );
    }

    @Test
    void dsl_query_two_root_properties()
    {
        final PropertyTree request = new PropertyTree();
        request.addSet( "fulltext", request.newSet() );
        request.addSet( "ngram", request.newSet() );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        assertThrows( IllegalArgumentException.class, () -> contentService.find( queryDsl ) );
    }

    @Test
    void dsl_query_sort()
    {
        final Content site = createContent( ContentPath.ROOT, "a" );

        final Content child3 = createContent( site.getPath(), "d" );
        final Content child2 = createContent( site.getPath(), "c" );
        final Content child1 = createContent( site.getPath(), "b" );

        final PropertyTree request = new PropertyTree();
        final PropertySet like = request.newSet();
        request.addSet( "like", like );
        like.addString( "field", "_path" );
        like.addString( "value", "*a/*" );

        PropertyTree order = new PropertyTree();
        order.addString( "field", "displayName" );
        order.addString( "direction", "DESC" );

        ContentQuery queryDsl =
            ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ), DslOrderExpr.from( order ) ) ).build();

        assertOrder( contentService.find( queryDsl ).getContentIds(), child3, child2, child1 );

        order = new PropertyTree();
        order.addString( "field", "displayName" );

        queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ), DslOrderExpr.from( order ) ) ).build();

        assertOrder( contentService.find(  queryDsl ).getContentIds(), child1, child2, child3 );
    }

    @Test
    void dsl_query_range()
    {
        final Content site = createContent( ContentPath.ROOT, "a" );

        final PropertyTree data1 = new PropertyTree();
        data1.addLong( "mylong", 5L );

        final PropertyTree data2 = new PropertyTree();
        data2.addLong( "mylong", 7L );

        final PropertyTree data3 = new PropertyTree();
        data2.addLong( "mylong", 9L );

        createContent( site.getPath(), "d", data1 );
        final Content child2 = createContent( site.getPath(), "c", data2 );
        createContent( site.getPath(), "b", data3 );

        final PropertyTree request = new PropertyTree();
        final PropertySet range = request.newSet();
        request.addSet( "range", range );
        range.addString( "field", "data.mylong" );
        range.addLong( "gte", 7L );
        range.addLong( "lt", 9L );

        PropertyTree order = new PropertyTree();
        order.addString( "field", "displayName" );
        order.addString( "direction", "DESC" );

        final ContentQuery queryDsl =
            ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ), DslOrderExpr.from( order ) ) ).build();

        assertOrder( contentService.find( queryDsl ).getContentIds(), child2 );
    }

    private FindContentIdsByQueryResult findContent( final ContentId contentId )
    {
        final ContentQuery query =
            ContentQuery.create().queryExpr( QueryParser.parse( "_id='" + contentId.toString() + "'" ) ).build();

        return contentService.find( query );
    }
}
