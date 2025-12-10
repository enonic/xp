package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentServiceImplTest_getByPath
    extends AbstractContentServiceTest
{

    @Test
    void test_pending_publish_master()
    {
        assertThrows( ContentNotFoundException.class, () -> ctxMaster().callWith( () -> {
            final Content content = createAndPublishContent( ContentPath.ROOT, Instant.now().plus( Duration.ofDays( 1 ) ) );

            return this.contentService.getByPath( content.getPath() );
        } ) );
    }

    @Test
    void test_publish_expired_master()
    {
        assertThrows( ContentNotFoundException.class, () -> ctxMaster().callWith( () -> {
            final Content content = createAndPublishContent( ContentPath.ROOT, Instant.now().minus( Duration.ofDays( 2 ) ), Instant.now().minus( Duration.ofDays( 1 ) ) );

            return this.contentService.getByPath( content.getPath() );
        } ) );
    }

    @Test
    void test_published_master()
    {
        ctxMaster().callWith( () -> {
            final Content content = createAndPublishContent( ContentPath.ROOT, Instant.now().minus( Duration.ofDays( 1 ) ), Instant.now().plus( Duration.ofDays( 1 ) ) );

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
