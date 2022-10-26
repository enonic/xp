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

public class ContentServiceImplTest_getById
    extends AbstractContentServiceTest
{

    @Test
    public void test_pending_publish_draft()
        throws Exception
    {
        final Content content =
            createContent( ContentPath.ROOT, ContentPublishInfo.create().from( Instant.now().plus( Duration.ofDays( 1 ) ) ).build() );

        assertNotNull( this.contentService.getById( content.getId() ) );
    }

    @Test
    public void test_pending_publish_master()
        throws Exception
    {
        assertThrows( ContentNotFoundException.class, () -> authorizedMasterContext().callWith( () -> {
            final Content content =
                createContent( ContentPath.ROOT, ContentPublishInfo.create().from( Instant.now().plus( Duration.ofDays( 1 ) ) ).build() );

            return this.contentService.getById( content.getId() );
        } ) );
    }

    @Test
    public void test_publish_expired_draft()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create()
            .from( Instant.now().minus( Duration.ofDays( 2 ) ) )
            .to( Instant.now().minus( Duration.ofDays( 1 ) ) )
            .build() );

        assertNotNull( this.contentService.getById( content.getId() ) );
    }

    @Test
    public void test_publish_expired_master()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create()
            .from( Instant.now().minus( Duration.ofDays( 2 ) ) )
            .to( Instant.now().minus( Duration.ofDays( 1 ) ) )
            .build() );

        assertThrows( ContentNotFoundException.class, () -> authorizedMasterContext().callWith( () -> {

            return this.contentService.getById( content.getId() );
        } ) );
    }

    @Test
    public void test_published_draft()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create()
            .from( Instant.now().minus( Duration.ofDays( 1 ) ) )
            .to( Instant.now().plus( Duration.ofDays( 1 ) ) )
            .build() );

        assertNotNull( this.contentService.getById( content.getId() ) );
    }

    @Test
    public void test_published_master()
        throws Exception
    {
        authorizedMasterContext().callWith( () -> {
            final Content content = createContent( ContentPath.ROOT, ContentPublishInfo.create()
                .from( Instant.now().minus( Duration.ofDays( 1 ) ) )
                .to( Instant.now().plus( Duration.ofDays( 1 ) ) )
                .build() );

            assertNotNull( this.contentService.getById( content.getId() ) );
            return null;
        } );
    }

    @Test
    public void test_get_content_from_wrong_context()
        throws Exception
    {
        final Content content = authorizedMasterContext().callWith( () -> createContent( ContentPath.ROOT, "my-content" ) );

        assertThrows( ContentNotFoundException.class, () -> ContextBuilder.from( authorizedMasterContext() )
            .attribute( ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE, NodePath.create( "archive" ).build() )
            .build()
            .callWith( () -> this.contentService.getById( content.getId() ) ) );
    }
}
