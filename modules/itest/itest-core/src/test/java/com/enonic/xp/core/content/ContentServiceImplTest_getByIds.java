package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentServiceImplTest_getByIds
    extends AbstractContentServiceTest
{

    @Test
    void test_pending_publish_draft()
    {
        final Content content1 = createContent( ContentPath.ROOT );
        final Content content2 = createContent( ContentPath.ROOT, ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );

        final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
        final Contents contents = this.contentService.getByIds( GetContentByIdsParams.create().contentIds( ids ).build() );

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

            final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
            final Contents contents = this.contentService.getByIds( GetContentByIdsParams.create().contentIds( ids ).build() );

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

        final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
        final Contents contents = this.contentService.getByIds( GetContentByIdsParams.create().contentIds( ids ).build() );

        assertThat( contents ).map( Content::getId ).containsExactlyInAnyOrder( content1.getId(), content2.getId() );
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

            final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
            final Contents contents = this.contentService.getByIds( GetContentByIdsParams.create().contentIds( ids ).build() );

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

        final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
        final Contents contents = this.contentService.getByIds( GetContentByIdsParams.create().contentIds( ids ).build() );

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

            final ContentIds ids = ContentIds.from( content1.getId(), content2.getId() );
            final Contents contents = this.contentService.getByIds( GetContentByIdsParams.create().contentIds( ids ).build() );

            assertThat( contents ).map( Content::getId ).containsExactlyInAnyOrder( content1.getId(), content2.getId() );
            return null;
        } );
    }
}
