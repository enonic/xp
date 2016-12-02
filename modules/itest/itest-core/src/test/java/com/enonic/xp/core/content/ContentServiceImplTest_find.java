package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.Assert.*;

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
    public void test_not_published_yet_draft()
        throws Exception
    {
        final FindContentByQueryResult result = createAndFindContent( ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );
        assertEquals( 1, result.getTotalHits() );
    }

    @Test
    public void test_not_published_yet_master()
        throws Exception
    {
        AUTHORIZED_MASTER_CONTEXT.callWith( () ->
                                            {
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
            to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            build() );
        assertEquals( 1, result.getTotalHits() );
    }

    @Test
    public void test_publish_expired_master()
        throws Exception
    {
        AUTHORIZED_MASTER_CONTEXT.callWith( () ->
                                            {
                                                final FindContentByQueryResult result = createAndFindContent( ContentPublishInfo.create().
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
        AUTHORIZED_MASTER_CONTEXT.callWith( () ->
                                            {
                                                final FindContentByQueryResult result = createAndFindContent( ContentPublishInfo.create().
                                                    from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                                                    to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
                                                    build() );

                                                assertEquals( 1, result.getTotalHits() );
                                                return null;
                                            } );
    }

    private FindContentByQueryResult createAndFindContent( final ContentPublishInfo publishInfo )
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, publishInfo );

        final ContentQuery query = ContentQuery.create().
            queryExpr( QueryParser.parse( "_id='" + content.getId().toString() + "'" ) ).
            build();

        return contentService.find( FindContentByQueryParams.create().
            contentQuery( query ).
            build() );
    }

}
