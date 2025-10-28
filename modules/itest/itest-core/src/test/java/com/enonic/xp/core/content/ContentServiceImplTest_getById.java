package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodePath;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentServiceImplTest_getById
    extends AbstractContentServiceTest
{

    @Test
    void test_pending_publish_draft()
    {
        final Content content =
            createContent( ContentPath.ROOT, ContentPublishInfo.create().from( Instant.now().plus( Duration.ofDays( 1 ) ) ).build() );

        assertNotNull( this.contentService.getById( content.getId() ) );
    }

    @Test
    void test_pending_publish_master()
    {
        assertThrows( ContentNotFoundException.class, () -> ctxMaster().callWith( () -> {
            final Content content =
                createContent( ContentPath.ROOT, ContentPublishInfo.create().from( Instant.now().plus( Duration.ofDays( 1 ) ) ).build() );

            return this.contentService.getById( content.getId() );
        } ) );
    }

    @Test
    void test_publish_expired_draft()
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create()
            .from( Instant.now().minus( Duration.ofDays( 2 ) ) )
            .to( Instant.now().minus( Duration.ofDays( 1 ) ) )
            .build() );

        assertNotNull( this.contentService.getById( content.getId() ) );
    }

    @Test
    void test_publish_expired_master()
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create()
            .from( Instant.now().minus( Duration.ofDays( 2 ) ) )
            .to( Instant.now().minus( Duration.ofDays( 1 ) ) )
            .build() );

        assertThrows( ContentNotFoundException.class, () -> ctxMaster().callWith( () -> {

            return this.contentService.getById( content.getId() );
        } ) );
    }

    @Test
    void test_published_draft()
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create()
            .from( Instant.now().minus( Duration.ofDays( 1 ) ) )
            .to( Instant.now().plus( Duration.ofDays( 1 ) ) )
            .build() );

        assertNotNull( this.contentService.getById( content.getId() ) );
    }

    @Test
    void test_published_master()
    {
        ctxMaster().callWith( () -> {
            final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create()
                .from( Instant.now().minus( Duration.ofDays( 1 ) ) )
                .to( Instant.now().plus( Duration.ofDays( 1 ) ) )
                .build() );

            assertNotNull( this.contentService.getById( content.getId() ) );
            return null;
        } );
    }

    @Test
    void test_get_content_from_wrong_context()
    {
        final Content content = ctxMasterSu().callWith( () -> createContent( ContentPath.ROOT, "my-content" ) );

        assertThrows( ContentNotFoundException.class, () -> ContextBuilder.from( ctxMasterSu() )
            .attribute( ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE, new NodePath( "/archive" ) )
            .build()
            .callWith( () -> this.contentService.getById( content.getId() ) ) );
    }
}
