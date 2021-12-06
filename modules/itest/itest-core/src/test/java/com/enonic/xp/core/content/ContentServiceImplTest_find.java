package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContentServiceImplTest_find
    extends AbstractContentServiceTest
{

    @Test
    public void order_by_path()
        throws Exception
    {
        final Content site = createContent( ContentPath.ROOT, "a" );

        final Content child3 = createContent( site.getPath(), "d" );
        final Content child2 = createContent( site.getPath(), "c" );
        final Content child1 = createContent( site.getPath(), "b" );

        final ContentQuery queryOrderAsc = ContentQuery.create().
            queryExpr( QueryParser.parse( "order by _path asc" ) ).
            build();

        assertOrder( contentService.find( FindContentByQueryParams.create().
            contentQuery( queryOrderAsc ).
            build() ), site, child1, child2, child3 );

        assertOrder( contentService.find( queryOrderAsc ).getContentIds(), site, child1, child2, child3 );

        final ContentQuery queryOrderDesc = ContentQuery.create().
            queryExpr( QueryParser.parse( "order by _path desc" ) ).
            build();

        assertOrder( contentService.find( FindContentByQueryParams.create().
            contentQuery( queryOrderDesc ).
            build() ), child3, child2, child1, site );

        assertOrder( contentService.find( queryOrderDesc ).getContentIds(), child3, child2, child1, site );
    }

    @Test
    public void test_pending_publish_draft()
        throws Exception
    {
        final FindContentByQueryResult result = createAndFindContent( ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );
        assertEquals( 1, result.getTotalHits() );
    }

    @Test
    public void test_pending_publish_master()
        throws Exception
    {
        authorizedMasterContext().callWith( () -> {
            final FindContentByQueryResult result = createAndFindContent( ContentPublishInfo.create().
                from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
                build() );
            assertEquals( 0, result.getTotalHits() );
            return null;
        } );
    }

    @Test
    public void test_publish_expired_draft()
        throws Exception
    {
        final FindContentByQueryResult result = createAndFindContent( ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            build() );
        assertEquals( 1, result.getTotalHits() );
    }

    @Test
    public void test_publish_expired_master()
        throws Exception
    {
        authorizedMasterContext().callWith( () -> {
            final FindContentByQueryResult result = createAndFindContent( ContentPublishInfo.create().
                from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                build() );
            assertEquals( 0, result.getTotalHits() );
            return null;
        } );
    }

    @Test
    public void test_published_draft()
        throws Exception
    {
        final FindContentByQueryResult result = createAndFindContent( ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );
        assertEquals( 1, result.getTotalHits() );
    }

    @Test
    public void test_published_master()
        throws Exception
    {
        authorizedMasterContext().callWith( () -> {
            final FindContentByQueryResult result = createAndFindContent( ContentPublishInfo.create().
                from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
                build() );

            assertEquals( 1, result.getTotalHits() );
            return null;
        } );
    }

    @Test
    public void multipleFilters()
        throws Exception
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
        assertEquals( 0, result.getHits() );
    }

    @Test
    public void dsl_query()
        throws Exception
    {
        final Content site = createContent( ContentPath.ROOT, "a" );

        final Content child3 = createContent( site.getPath(), "d" );
        final Content child2 = createContent( site.getPath(), "c" );
        final Content child1 = createContent( site.getPath(), "b" );

        final PropertyTree request = new PropertyTree();
        final PropertySet fulltext = new PropertySet();
        fulltext.addString( "field", "displayName" );
        fulltext.addString( "query", "c" );

        request.addSet( "fulltext", fulltext );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        assertOrder( contentService.find( FindContentByQueryParams.create().contentQuery( queryDsl ).build() ), child2 );
    }

    @Test
    public void dsl_query_empty()
        throws Exception
    {
        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( new PropertyTree() ) ) ).build();

        assertThrows( IllegalArgumentException.class,
                      () -> contentService.find( FindContentByQueryParams.create().contentQuery( queryDsl ).build() ) );
    }

    @Test
    public void dsl_query_two_root_properties()
        throws Exception
    {
        final PropertyTree request = new PropertyTree();
        request.addSet( "fulltext", new PropertySet() );
        request.addSet( "ngram", new PropertySet() );

        final ContentQuery queryDsl = ContentQuery.create().queryExpr( QueryExpr.from( DslExpr.from( request ) ) ).build();

        assertThrows( IllegalArgumentException.class,
                      () -> contentService.find( FindContentByQueryParams.create().contentQuery( queryDsl ).build() ) );
    }

    private FindContentByQueryResult createAndFindContent( final ContentPublishInfo publishInfo )
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, publishInfo );

        final ContentQuery query =
            ContentQuery.create().queryExpr( QueryParser.parse( "_id='" + content.getId().toString() + "'" ) ).build();

        return contentService.find( FindContentByQueryParams.create().contentQuery( query ).build() );
    }

}
