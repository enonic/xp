package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;

import static org.junit.Assert.*;

public class ContentServiceImplTest_contentExists
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void test_pending_publish_draft()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        assertTrue( this.contentService.contentExists( content.getId() ) );
        assertTrue( this.contentService.contentExists( content.getPath() ) );
    }

    @Test
    public void test_pending_publish_master()
        throws Exception
    {
        final Content content = AUTHORIZED_MASTER_CONTEXT.callWith( () -> createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() ) );
        MASTER_CONTEXT.callWith( () -> {
            assertFalse( contentService.contentExists( content.getId() ) );
            assertFalse( contentService.contentExists( content.getPath() ) );
            return null;
        } );
    }

    @Test
    public void test_publish_expired_draft()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            build() );

        assertTrue( this.contentService.contentExists( content.getId() ) );
        assertTrue( this.contentService.contentExists( content.getPath() ) );
    }

    @Test
    public void test_publish_expired_master()
        throws Exception
    {
        final Content content = AUTHORIZED_MASTER_CONTEXT.callWith( () -> createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            build() ) );
        MASTER_CONTEXT.callWith( () -> {
            assertFalse( this.contentService.contentExists( content.getId() ) );
            assertFalse( this.contentService.contentExists( content.getPath() ) );
            return null;
        } );
    }

    @Test
    public void test_published_draft()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        assertTrue( this.contentService.contentExists( content.getId() ) );
        assertTrue( this.contentService.contentExists( content.getPath() ) );
    }

    @Test
    public void test_published_master()
        throws Exception
    {
        final Content content = AUTHORIZED_MASTER_CONTEXT.callWith( () -> createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() ) );
        MASTER_CONTEXT.callWith( () -> {
            assertTrue( this.contentService.contentExists( content.getId() ) );
            assertTrue( this.contentService.contentExists( content.getPath() ) );
            return null;
        } );
    }
}
