package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.Contents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentServiceImplTest_getByPaths
    extends AbstractContentServiceTest
{

    @Test
    void empty()
    {

        final Contents contents = this.contentService.getByPaths( ContentPaths.empty() );

        assertNotNull( contents );
        assertEquals( 0, contents.getSize() );
    }

    @Test
    void one()
    {

        final Content content = createContent( ContentPath.ROOT );

        final Contents contents = this.contentService.getByPaths( ContentPaths.from( content.getPath() ) );

        assertNotNull( contents );
        assertEquals( 1, contents.getSize() );
    }

    @Test
    void multiple()
    {

        final Content content1 = createContent( ContentPath.ROOT );
        final Content content2 = createContent( content1.getPath() );
        final Content content3 = createContent( content2.getPath() );

        final Contents contents =
            this.contentService.getByPaths( ContentPaths.from( content1.getPath(), content2.getPath(), content3.getPath() ) );

        assertNotNull( contents );
        assertEquals( 3, contents.getSize() );
    }

    @Test
    void invalid_path()
    {

        final Contents contents = this.contentService.getByPaths( ContentPaths.from( "/test_invalid_path" ) );

        assertNotNull( contents );
        assertEquals( 0, contents.getSize() );
    }

    @Test
    void invalid_and_valid_path()
    {

        final Content content1 = createContent( ContentPath.ROOT );
        final Content content2 = createContent( content1.getPath() );
        final Contents contents = this.contentService.getByPaths(
            ContentPaths.from( "/test_invalid_path", content1.getPath().toString(), content2.getPath().toString() ) );

        assertNotNull( contents );
        assertEquals( 2, contents.getSize() );
    }

    @Test
    void test_pending_publish_draft()
    {
        final Content content1 = createContent( ContentPath.ROOT );
        final Content content2 = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        final ContentPaths paths = ContentPaths.from( content1.getPath(), content2.getPath() );
        final Contents contents = this.contentService.getByPaths( paths );

        assertEquals( contents.getSize(), 2 );
        assertTrue( contents.contains( content1 ) );
        assertTrue( contents.contains( content2 ) );
    }

    @Test
    void test_pending_publish_master()
    {
        ctxMaster().callWith( () -> {
            final Content content1 = createContent( ContentPath.ROOT );
            final Content content2 = createContent( ContentPath.ROOT, ContentPublishInfo.create().
                from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
                build() );

            final ContentPaths paths = ContentPaths.from( content1.getPath(), content2.getPath() );
            final Contents contents = this.contentService.getByPaths( paths );

            assertThat( contents ).map( Content::getId ).containsExactly( content1.getId() );
            return null;
        } );
    }

    @Test
    void test_publish_expired_draft()
    {
        final Content content1 = createContent( ContentPath.ROOT );
        final Content content2 = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 2 ) ) ).
            to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            build() );

        final ContentPaths paths = ContentPaths.from( content1.getPath(), content2.getPath() );
        final Contents contents = this.contentService.getByPaths( paths );

        assertEquals( contents.getSize(), 2 );
        assertTrue( contents.contains( content1 ) );
        assertTrue( contents.contains( content2 ) );
    }

    @Test
    void test_publish_expired_master()
    {
        ctxMaster().callWith( () -> {
            final Content content1 = createContent( ContentPath.ROOT );
            final Content content2 = createContent( ContentPath.ROOT, ContentPublishInfo.create().
                from( Instant.now().minus( Duration.ofDays( 2 ) ) ).
                to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                build() );

            final ContentPaths paths = ContentPaths.from( content1.getPath(), content2.getPath() );
            final Contents contents = this.contentService.getByPaths( paths );

            assertThat( contents ).map( Content::getId ).containsExactly( content1.getId() );
            return null;
        } );
    }

    @Test
    void test_published_draft()
    {
        final Content content1 = createContent( ContentPath.ROOT );
        final Content content2 = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        final ContentPaths paths = ContentPaths.from( content1.getPath(), content2.getPath() );
        final Contents contents = this.contentService.getByPaths( paths );

        assertEquals( contents.getSize(), 2 );
        assertTrue( contents.contains( content1 ) );
        assertTrue( contents.contains( content2 ) );
    }

    @Test
    void test_published_master()
    {
        ctxMaster().callWith( () -> {
            final Content content1 = createContent( ContentPath.ROOT );
            final Content content2 = createContent( ContentPath.ROOT, ContentPublishInfo.create().
                from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
                build() );

            final ContentPaths paths = ContentPaths.from( content1.getPath(), content2.getPath() );
            final Contents contents = this.contentService.getByPaths( paths );

            assertThat( contents ).map( Content::getId ).containsExactlyInAnyOrder( content1.getId(), content2.getId() );

            return null;
        } );
    }
}
