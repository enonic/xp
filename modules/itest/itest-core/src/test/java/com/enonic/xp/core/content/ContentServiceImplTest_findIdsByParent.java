package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentIdsByParentResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentServiceImplTest_findIdsByParent
    extends AbstractContentServiceTest
{

    @Test
    void root_content()
    {
        createContent( ContentPath.ROOT );
        createContent( ContentPath.ROOT );

        final FindContentIdsByParentResult result =
            contentService.findIdsByParent( FindContentByParentParams.create().from( 0 ).size( 30 ).parentPath( null ).build() );

        assertNotNull( result );
        assertEquals( 2, result.getTotalHits() );

    }

    @Test
    void root_no_content()
    {
        final FindContentByParentParams params = FindContentByParentParams.create().from( 0 ).size( 30 ).parentPath( null ).build();

        final FindContentIdsByParentResult result = contentService.findIdsByParent( params );

        assertNotNull( result );
        assertEquals( 0, result.getTotalHits() );

    }

    @Test
    void root_children()
    {

        final Content parentContent = createContent( ContentPath.ROOT );
        final Content content1 = createContent( parentContent.getPath() );
        final Content content2 = createContent( parentContent.getPath() );
        final Content content3 = createContent( parentContent.getPath() );

        final ContentPath parentContentPath = parentContent.getPath();

        final FindContentByParentParams params =
            FindContentByParentParams.create().from( 0 ).size( 30 ).parentPath( parentContentPath ).build();

        final FindContentIdsByParentResult result = contentService.findIdsByParent( params );

        assertNotNull( result );
        assertEquals( 3, result.getTotalHits() );

    }

    @Test
    void deep_children()
    {

        final Content rootContent = createContent( ContentPath.ROOT );
        final Content childrenLevel1 = createContent( rootContent.getPath() );
        final Content childrenLevel2_1 = createContent( childrenLevel1.getPath() );
        final Content childrenLevel2_2 = createContent( childrenLevel1.getPath() );
        final Content childrenLevel2_3 = createContent( childrenLevel1.getPath() );

        final ContentPath parentContentPath = childrenLevel1.getPath();

        final FindContentByParentParams params =
            FindContentByParentParams.create().from( 0 ).size( 30 ).parentPath( parentContentPath ).build();

        final FindContentIdsByParentResult result = contentService.findIdsByParent( params );

        assertNotNull( result );
        assertEquals( 3, result.getTotalHits() );

    }

    @Test
    void invalid_parent_path()
    {
        final Content rootContent = createContent( ContentPath.ROOT );
        final Content childrenLevel1 = createContent( rootContent.getPath() );

        final FindContentByParentParams params =
            FindContentByParentParams.create().from( 0 ).size( 30 ).parentPath( ContentPath.from( "/test_invalid_path" ) ).build();

        final FindContentIdsByParentResult result = contentService.findIdsByParent( params );

        assertNotNull( result );
        assertEquals( 0, result.getTotalHits() );

    }

    @Test
    void params_size_zero()
    {
        final Content parentContent = createContent( ContentPath.ROOT );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );

        final FindContentByParentParams params =
            FindContentByParentParams.create().from( 0 ).size( 0 ).parentPath( parentContent.getPath() ).build();

        final FindContentIdsByParentResult result = contentService.findIdsByParent( params );

        assertNotNull( result );
        assertEquals( 3, result.getTotalHits() );
        assertTrue( result.getContentIds().isEmpty() );

    }

    @Test
    void params_size_one()
    {

        final Content parentContent = createContent( ContentPath.ROOT );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );

        final ContentPath parentContentPath = parentContent.getPath();

        final FindContentByParentParams params =
            FindContentByParentParams.create().from( 0 ).size( 1 ).parentPath( parentContentPath ).build();

        final FindContentIdsByParentResult result = contentService.findIdsByParent( params );

        assertNotNull( result );
        assertEquals( 3, result.getTotalHits() );
        assertEquals( 1, result.getContentIds().getSize() );

    }

    @Test
    void params_from_beyond()
    {
        final Content parentContent = createContent( ContentPath.ROOT );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );

        final FindContentIdsByParentResult result =
            contentService.findIdsByParent( FindContentByParentParams.create().from( 10 ).parentPath( parentContent.getPath() ).build() );

        assertNotNull( result );
        assertEquals( 3, result.getTotalHits() );
        assertTrue( result.getContentIds().isEmpty() );
    }

    @Test
    void params_from()
    {

        final Content parentContent = createContent( ContentPath.ROOT );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );

        final ContentPath parentContentPath = parentContent.getPath();

        final FindContentByParentParams params = FindContentByParentParams.create().from( 2 ).parentPath( parentContentPath ).build();

        final FindContentIdsByParentResult result = contentService.findIdsByParent( params );

        assertNotNull( result );
        assertEquals( 4, result.getTotalHits() );
        assertEquals( 2, result.getContentIds().getSize() );
    }


    @Test
    void test_pending_publish_draft()
        throws Exception
    {
        final FindContentIdsByParentResult result =
            createAndFindContent( ContentPublishInfo.create().from( Instant.now().plus( Duration.ofDays( 1 ) ) ).build() );
        assertEquals( 1, result.getTotalHits() );
    }

    @Test
    void test_pending_publish_master()
    {
        ctxMaster().callWith( () -> {
            final FindContentIdsByParentResult result =
                createAndFindContent( ContentPublishInfo.create().from( Instant.now().plus( Duration.ofDays( 1 ) ) ).build() );
            assertEquals( 0, result.getTotalHits() );
            return null;
        } );
    }

    @Test
    void test_publish_expired_draft()
        throws Exception
    {
        final FindContentIdsByParentResult result = createAndFindContent( ContentPublishInfo.create()
                                                                              .from( Instant.now().minus( Duration.ofDays( 2 ) ) )
                                                                              .to( Instant.now().minus( Duration.ofDays( 1 ) ) )
                                                                              .build() );
        assertEquals( 1, result.getTotalHits() );
    }

    @Test
    void test_publish_expired_master()
    {
        ctxMaster().callWith( () -> {
            final FindContentIdsByParentResult result = createAndFindContent( ContentPublishInfo.create()
                                                                                  .from( Instant.now().minus( Duration.ofDays( 2 ) ) )
                                                                                  .to( Instant.now().minus( Duration.ofDays( 1 ) ) )
                                                                                  .build() );
            assertEquals( 0, result.getTotalHits() );
            return null;
        } );
    }

    @Test
    void test_published_draft()
        throws Exception
    {
        final FindContentIdsByParentResult result = createAndFindContent( ContentPublishInfo.create()
                                                                              .from( Instant.now().minus( Duration.ofDays( 1 ) ) )
                                                                              .to( Instant.now().plus( Duration.ofDays( 1 ) ) )
                                                                              .build() );
        assertEquals( 1, result.getTotalHits() );
    }

    @Test
    void test_published_master()
    {
        ctxMaster().callWith( () -> {
            final FindContentIdsByParentResult result = createAndFindContent( ContentPublishInfo.create()
                                                                                  .from( Instant.now().minus( Duration.ofDays( 1 ) ) )
                                                                                  .to( Instant.now().plus( Duration.ofDays( 1 ) ) )
                                                                                  .build() );

            assertEquals( 1, result.getTotalHits() );
            return null;
        } );
    }

    private FindContentIdsByParentResult createAndFindContent( final ContentPublishInfo publishInfo )
    {
        createContent( ContentPath.ROOT, publishInfo );

        final FindContentByParentParams params = FindContentByParentParams.create().parentPath( ContentPath.ROOT ).build();

        return contentService.findIdsByParent( params );
    }

}
