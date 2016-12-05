package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;

import static org.junit.Assert.*;

public class ContentServiceImplTest_getByIds
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
        final Content content1 = createContent( ContentPath.ROOT );
        final Content content2 = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
        final Contents contents = this.contentService.getByIds( new GetContentByIdsParams( ids ) );

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

            final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
            final Contents contents = this.contentService.getByIds( new GetContentByIdsParams( ids ) );

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

        final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
        final Contents contents = this.contentService.getByIds( new GetContentByIdsParams( ids ) );

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

            final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
            final Contents contents = this.contentService.getByIds( new GetContentByIdsParams( ids ) );

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

        final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
        final Contents contents = this.contentService.getByIds( new GetContentByIdsParams( ids ) );

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

            final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
            final Contents contents = this.contentService.getByIds( new GetContentByIdsParams( ids ) );

            assertEquals( contents.getSize(), 2 );
            assertTrue( contents.contains( content1 ) );
            assertTrue( contents.contains( content2 ) );
            return null;
        } );
    }
}
