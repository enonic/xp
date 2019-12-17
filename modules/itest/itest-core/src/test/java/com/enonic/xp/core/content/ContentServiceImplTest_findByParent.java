package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.node.RefreshMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentServiceImplTest_findByParent
    extends AbstractContentServiceTest
{

    @Test
    public void root_content()
        throws Exception
    {
        createContent( ContentPath.ROOT );
        createContent( ContentPath.ROOT );

        this.nodeService.refresh( RefreshMode.SEARCH );

        final FindContentByParentResult result = contentService.findByParent( FindContentByParentParams.create().
            from( 0 ).
            size( 30 ).
            parentPath( null ).
            build() );

        assertNotNull( result );
        assertEquals( 2, result.getTotalHits() );

    }

    @Test
    public void root_no_content()
        throws Exception
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
    public void root_children()
        throws Exception
    {

        final Content parentContent = createContent( ContentPath.ROOT );
        final Content content1 = createContent( parentContent.getPath() );
        final Content content2 = createContent( parentContent.getPath() );
        final Content content3 = createContent( parentContent.getPath() );

        final ContentPath parentContentPath = parentContent.getPath();

        this.nodeService.refresh( RefreshMode.SEARCH );

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
    public void deep_children()
        throws Exception
    {

        final Content rootContent = createContent( ContentPath.ROOT );
        final Content childrenLevel1 = createContent( rootContent.getPath() );
        final Content childrenLevel2_1 = createContent( childrenLevel1.getPath() );
        final Content childrenLevel2_2 = createContent( childrenLevel1.getPath() );
        final Content childrenLevel2_3 = createContent( childrenLevel1.getPath() );

        final ContentPath parentContentPath = childrenLevel1.getPath();

        this.nodeService.refresh( RefreshMode.SEARCH );

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
    public void invalid_parent_path()
        throws Exception
    {
        final Content rootContent = createContent( ContentPath.ROOT );
        final Content childrenLevel1 = createContent( rootContent.getPath() );

        this.nodeService.refresh( RefreshMode.SEARCH );

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
    public void params_size_zero()
        throws Exception
    {
        final Content parentContent = createContent( ContentPath.ROOT );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );

        this.nodeService.refresh( RefreshMode.SEARCH );

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 0 ).
            parentPath( parentContent.getPath() ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

        assertNotNull( result );
        assertEquals( 0, result.getHits() );
        assertEquals( 3, result.getTotalHits() );
        assertTrue( result.getContents().isEmpty() );

    }

    @Test
    public void params_size_one()
        throws Exception
    {

        final Content parentContent = createContent( ContentPath.ROOT );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );

        final ContentPath parentContentPath = parentContent.getPath();

        this.nodeService.refresh( RefreshMode.SEARCH );

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 1 ).
            parentPath( parentContentPath ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

        assertNotNull( result );
        assertEquals( 1, result.getHits() );
        assertEquals( 3, result.getTotalHits() );
        assertEquals( 1, result.getContents().getSize() );

    }

    @Test
    public void params_from_beyond()
        throws Exception
    {
        final Content parentContent = createContent( ContentPath.ROOT );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );

        refresh();

        this.nodeService.refresh( RefreshMode.SEARCH );

        final FindContentByParentResult result = contentService.findByParent( FindContentByParentParams.create().
            from( 10 ).
            parentPath( parentContent.getPath() ).
            build() );

        assertNotNull( result );
        assertEquals( 0, result.getHits() );
        assertEquals( 3, result.getTotalHits() );
        assertTrue( result.getContents().isEmpty() );
    }

    @Test
    public void params_from()
        throws Exception
    {

        final Content parentContent = createContent( ContentPath.ROOT );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );
        createContent( parentContent.getPath() );

        final ContentPath parentContentPath = parentContent.getPath();

        this.nodeService.refresh( RefreshMode.SEARCH );

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 2 ).
            parentPath( parentContentPath ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

        assertNotNull( result );
        assertEquals( 2, result.getHits() );
        assertEquals( 4, result.getTotalHits() );
        assertEquals( 2, result.getContents().getSize() );
    }


    @Test
    public void hasChildResolved()
        throws Exception
    {
        final Content parentContent = createContent( ContentPath.ROOT );
        final Content content1 = createContent( parentContent.getPath() );
        createContent( content1.getPath() );

        final ContentPath parentContentPath = parentContent.getPath();

        this.nodeService.refresh( RefreshMode.SEARCH );

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 30 ).
            parentPath( parentContentPath ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

        assertNotNull( result );
        assertEquals( 1, result.getTotalHits() );
        final Content content1Result = result.getContents().getContentById( content1.getId() );
        assertTrue( content1Result.hasChildren() );
    }

    @Test
    public void test_pending_publish_draft()
        throws Exception
    {
        final FindContentByParentResult result = createAndFindContent( ContentPublishInfo.create().
            from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );
        assertEquals( 1, result.getTotalHits() );
    }

    @Test
    public void test_pending_publish_master()
        throws Exception
    {
        AUTHORIZED_MASTER_CONTEXT.callWith( () -> {
            final FindContentByParentResult result = createAndFindContent( ContentPublishInfo.create().
                from( Instant.now().plus( Duration.ofDays( 1 ) ) ).
                build() );
            assertEquals( 0, result.getTotalHits() );
            return null;
        } );
    }

    @Test
    public void test_publish_expired_draft()
        throws Exception
    {
        final FindContentByParentResult result = createAndFindContent( ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            build() );
        assertEquals( 1, result.getTotalHits() );
    }

    @Test
    public void test_publish_expired_master()
        throws Exception
    {
        AUTHORIZED_MASTER_CONTEXT.callWith( () -> {
            final FindContentByParentResult result = createAndFindContent( ContentPublishInfo.create().
                from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                to( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                build() );
            assertEquals( 0, result.getTotalHits() );
            return null;
        } );
    }

    @Test
    public void test_published_draft()
        throws Exception
    {
        final FindContentByParentResult result = createAndFindContent( ContentPublishInfo.create().
            from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
            to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
            build() );
        assertEquals( 1, result.getTotalHits() );
    }

    @Test
    public void test_published_master()
        throws Exception
    {
        AUTHORIZED_MASTER_CONTEXT.callWith( () -> {
            final FindContentByParentResult result = createAndFindContent( ContentPublishInfo.create().
                from( Instant.now().minus( Duration.ofDays( 1 ) ) ).
                to( Instant.now().plus( Duration.ofDays( 1 ) ) ).
                build() );

            assertEquals( 1, result.getTotalHits() );
            return null;
        } );
    }

    private FindContentByParentResult createAndFindContent( final ContentPublishInfo publishInfo )
        throws Exception
    {
        createContent( ContentPath.ROOT, publishInfo );

        this.nodeService.refresh( RefreshMode.SEARCH );

        final FindContentByParentParams params = FindContentByParentParams.create().
            parentPath( ContentPath.ROOT ).
            build();

        return contentService.findByParent( params );
    }

}
