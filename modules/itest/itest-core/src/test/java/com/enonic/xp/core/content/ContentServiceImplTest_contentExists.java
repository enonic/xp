package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentServiceImplTest_contentExists
    extends AbstractContentServiceTest
{

    @Test
    void test_pending_publish_draft()
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        assertTrue( this.contentService.contentExists( content.getId() ) );
        assertTrue( this.contentService.contentExists( content.getPath() ) );
    }

    @Test
    void test_pending_publish_master()
    {
        final Content content = ctxMaster().callWith( () -> createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() ) );
        ctxMasterAnonymous().callWith( () -> {
            assertFalse( contentService.contentExists( content.getId() ) );
            assertFalse( contentService.contentExists( content.getPath() ) );
            return null;
        } );
    }

    @Test
    void test_publish_expired_draft()
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 2 ) ) ).
            to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            build() );

        assertTrue( this.contentService.contentExists( content.getId() ) );
        assertTrue( this.contentService.contentExists( content.getPath() ) );
    }

    @Test
    void test_publish_expired_master()
    {
        final Content content = ctxMaster().callWith( () -> createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 2 ) ) ).
            to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            build() ) );
        ctxMasterAnonymous().callWith( () -> {
            assertFalse( this.contentService.contentExists( content.getId() ) );
            assertFalse( this.contentService.contentExists( content.getPath() ) );
            return null;
        } );
    }

    @Test
    void test_published_draft()
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        assertTrue( this.contentService.contentExists( content.getId() ) );
        assertTrue( this.contentService.contentExists( content.getPath() ) );
    }

    @Test
    void test_published_master()
    {
        final Content content = ctxMaster().callWith( () -> createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() ) );
        ctxMasterAnonymous().callWith( () -> {
            assertTrue( this.contentService.contentExists( content.getId() ) );
            assertTrue( this.contentService.contentExists( content.getPath() ) );
            return null;
        } );
    }
}
