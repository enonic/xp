package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentServiceImplTest_getByPath
    extends AbstractContentServiceTest
{

    @Test
    void test_pending_publish_draft()
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        assertNotNull( this.contentService.getByPath( content.getPath() ) );
    }

    @Test
    void test_pending_publish_master()
    {
        assertThrows( ContentNotFoundException.class, () -> ctxMaster().callWith( () -> {
            final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
                from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
                build() );

            return this.contentService.getByPath( content.getPath() );
        } ) );
    }

    @Test
    void test_publish_expired_draft()
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 2 ) ) ).
            to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            build() );

        assertNotNull( this.contentService.getByPath( content.getPath() ) );
    }

    @Test
    void test_publish_expired_master()
    {
        assertThrows( ContentNotFoundException.class, () -> ctxMaster().callWith( () -> {
            final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
                from( Instant.now().minus( Duration.ofDays( 2 ) ) ).
                to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                build() );

            return this.contentService.getByPath( content.getPath() );
        } ) );
    }

    @Test
    void test_published_draft()
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        assertNotNull( this.contentService.getByPath( content.getPath() ) );
    }

    @Test
    void test_published_master()
    {
        ctxMaster().callWith( () -> {
            final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create()
                .from( Instant.now().minus( Duration.ofDays( 1 ) ) )
                .to( Instant.now().plus( Duration.ofDays( 1 ) ) )
                .build() );

            assertNotNull( this.contentService.getByPath( content.getPath() ) );
            return null;
        } );
    }

    @Test
    void test_root()
    {
        final Content content = contentService.getByPath( ContentPath.ROOT );

        assertEquals( ContentPath.ROOT, content.getPath() );
    }

}
