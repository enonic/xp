package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContentServiceImplTest_getByPath
    extends AbstractContentServiceTest
{

    @Test
    public void test_pending_publish_draft()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        assertNotNull( this.contentService.getByPath( content.getPath() ) );
    }

    @Test
    public void test_pending_publish_master()
        throws Exception
    {
        assertThrows(ContentNotFoundException.class, () -> AUTHORIZED_MASTER_CONTEXT.callWith( () -> {
            final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
                from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
                build() );

            return this.contentService.getByPath( content.getPath() );
        } ));
    }

    @Test
    public void test_publish_expired_draft()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            build() );

        assertNotNull( this.contentService.getByPath( content.getPath() ) );
    }

    @Test
    public void test_publish_expired_master()
        throws Exception
    {
        assertThrows(ContentNotFoundException.class, () -> AUTHORIZED_MASTER_CONTEXT.callWith( () -> {
            final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
                from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                build() );

            return this.contentService.getByPath( content.getPath() );
        } ));
    }

    @Test
    public void test_published_draft()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        assertNotNull( this.contentService.getByPath( content.getPath() ) );
    }

    @Test
    public void test_published_master()
        throws Exception
    {
        AUTHORIZED_MASTER_CONTEXT.callWith( () -> {
            final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create().
                from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
                build() );

            assertNotNull( this.contentService.getByPath( content.getPath() ) );
            return null;
        } );
    }
}
