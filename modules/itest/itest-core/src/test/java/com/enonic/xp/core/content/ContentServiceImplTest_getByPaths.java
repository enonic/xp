package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.Contents;

import static org.junit.Assert.*;

public class ContentServiceImplTest_getByPaths
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void empty()
        throws Exception
    {

        final Contents contents = this.contentService.getByPaths( ContentPaths.empty() );

        assertNotNull( contents );
        assertEquals( 0, contents.getSize() );
    }

    @Test
    public void one()
        throws Exception
    {

        final Content content = createContent( ContentPath.ROOT );

        final Contents contents = this.contentService.getByPaths( ContentPaths.from( content.getPath() ) );

        assertNotNull( contents );
        assertEquals( 1, contents.getSize() );
    }

    @Test
    public void multiple()
        throws Exception
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
    public void invalid_path()
        throws Exception
    {

        final Contents contents = this.contentService.getByPaths( ContentPaths.from( "/test_invalid_path" ) );

        assertNotNull( contents );
        assertEquals( 0, contents.getSize() );
    }

    @Test
    public void invalid_and_valid_path()
        throws Exception
    {

        final Content content1 = createContent( ContentPath.ROOT );
        final Content content2 = createContent( content1.getPath() );
        final Contents contents = this.contentService.getByPaths(
            ContentPaths.from( "/test_invalid_path", content1.getPath().toString(), content2.getPath().toString() ) );

        assertNotNull( contents );
        assertEquals( 2, contents.getSize() );
    }

    @Test
    public void test_pending_publish_draft()
        throws Exception
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
    public void test_pending_publish_master()
        throws Exception
    {
        AUTHORIZED_MASTER_CONTEXT.callWith( () -> {
            final Content content1 = createContent( ContentPath.ROOT );
            final Content content2 = createContent( ContentPath.ROOT, ContentPublishInfo.create().
                from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
                build() );

            final ContentPaths paths = ContentPaths.from( content1.getPath(), content2.getPath() );
            final Contents contents = this.contentService.getByPaths( paths );

            assertEquals( contents.getSize(), 1 );
            assertTrue( contents.contains( content1 ) );
            assertFalse( contents.contains( content2 ) );

            return null;
        } );
    }

    @Test
    public void test_publish_expired_draft()
        throws Exception
    {
        final Content content1 = createContent( ContentPath.ROOT );
        final Content content2 = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            build() );

        final ContentPaths paths = ContentPaths.from( content1.getPath(), content2.getPath() );
        final Contents contents = this.contentService.getByPaths( paths );

        assertEquals( contents.getSize(), 2 );
        assertTrue( contents.contains( content1 ) );
        assertTrue( contents.contains( content2 ) );
    }

    @Test
    public void test_publish_expired_master()
        throws Exception
    {
        AUTHORIZED_MASTER_CONTEXT.callWith( () -> {
            final Content content1 = createContent( ContentPath.ROOT );
            final Content content2 = createContent( ContentPath.ROOT, ContentPublishInfo.create().
                from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                build() );

            final ContentPaths paths = ContentPaths.from( content1.getPath(), content2.getPath() );
            final Contents contents = this.contentService.getByPaths( paths );

            assertEquals( contents.getSize(), 1 );
            assertTrue( contents.contains( content1 ) );
            assertFalse( contents.contains( content2 ) );
            return null;
        } );
    }

    @Test
    public void test_published_draft()
        throws Exception
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
    public void test_published_master()
        throws Exception
    {
        AUTHORIZED_MASTER_CONTEXT.callWith( () -> {
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
            return null;
        } );
    }
}
