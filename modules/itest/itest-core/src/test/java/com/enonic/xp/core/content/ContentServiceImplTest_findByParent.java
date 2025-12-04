package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentServiceImplTest_findByParent
    extends AbstractContentServiceTest
{

    @Test
    void root_content()
    {
        createContent( ContentPath.ROOT );
        createContent( ContentPath.ROOT );

        final FindContentByParentResult result = contentService.findByParent( FindContentByParentParams.create().
            from( 0 ).
            size( 30 ).
            parentPath( null ).
            build() );

        assertNotNull( result );
        assertEquals( 2, result.getTotalHits() );

    }

    @Test
    void root_no_content()
    {
        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 30 ).
            parentPath( null ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

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

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 30 ).
            parentPath( parentContentPath ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

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

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 30 ).
            parentPath( parentContentPath ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

        assertNotNull( result );
        assertEquals( 3, result.getTotalHits() );

    }

    @Test
    void invalid_parent_path()
    {
        final Content rootContent = createContent( ContentPath.ROOT );
        final Content childrenLevel1 = createContent( rootContent.getPath() );

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 30 ).
            parentPath( ContentPath.from( "/test_invalid_path" ) ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

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

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 0 ).
            parentPath( parentContent.getPath() ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

        assertNotNull( result );
        assertEquals( 3, result.getTotalHits() );
        assertTrue( result.getContents().isEmpty() );

    }

    @Test
    void params_size_one()
    {

        final Content parentContent = createContent( ContentPath.ROOT );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );

        final ContentPath parentContentPath = parentContent.getPath();

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 1 ).
            parentPath( parentContentPath ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

        assertNotNull( result );
        assertEquals( 3, result.getTotalHits() );
        assertEquals( 1, result.getContents().getSize() );

    }

    @Test
    void params_from_beyond()
    {
        final Content parentContent = createContent( ContentPath.ROOT );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );

        final FindContentByParentResult result = contentService.findByParent( FindContentByParentParams.create().
            from( 10 ).
            parentPath( parentContent.getPath() ).
            build() );

        assertNotNull( result );
        assertEquals( 3, result.getTotalHits() );
        assertTrue( result.getContents().isEmpty() );
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

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 2 ).
            parentPath( parentContentPath ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

        assertNotNull( result );
        assertEquals( 4, result.getTotalHits() );
        assertEquals( 2, result.getContents().getSize() );
    }


    @Test
    void test_pending_publish_master()
    {
        ctxMaster().runWith( () -> {
            createAndPublishContent( ContentPath.ROOT, Instant.now().plus( Duration.ofDays( 1 ) ) );
            final FindContentByParentResult result = findByParent();
            assertEquals( 0, result.getTotalHits() );
        } );
    }

    @Test
    void test_publish_expired_master()
    {
        ctxMaster().runWith( () -> {
            createAndPublishContent( ContentPath.ROOT, Instant.now().minus( Duration.ofDays( 2 ) ) , Instant.now().minus( Duration.ofDays( 1 ) ) );
            final FindContentByParentResult result = findByParent();
            assertEquals( 0, result.getTotalHits() );
        } );
    }

    @Test
    void test_published_master()
    {
        ctxMaster().runWith( () -> {
            createAndPublishContent( ContentPath.ROOT, Instant.now().minus( Duration.ofDays( 1 ) ), Instant.now().plus( Duration.ofDays( 1 ) ) );
            final FindContentByParentResult result = findByParent();

            assertEquals( 1, result.getTotalHits() );
        } );
    }

    private FindContentByParentResult findByParent()
    {
        final FindContentByParentParams params = FindContentByParentParams.create().parentPath( ContentPath.ROOT ).build();

        return contentService.findByParent( params );
    }
}
