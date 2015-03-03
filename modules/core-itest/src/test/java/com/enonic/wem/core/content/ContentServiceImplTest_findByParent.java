package com.enonic.wem.core.content;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ContentServiceImplTest_findByParent extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void root_content() throws Exception{

        final Content content1 = createContent(ContentPath.ROOT);
        final Content content2 = createContent(ContentPath.ROOT);

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 30 ).
            parentPath( null ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

        assertNotNull( result );
        assertEquals( 2, result.getTotalHits() );

    }

    @Test
    public void root_no_content() throws Exception{

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
    public void root_children() throws Exception{

        final Content parentContent = createContent(ContentPath.ROOT);
        final Content content1 = createContent(parentContent.getPath());
        final Content content2 = createContent(parentContent.getPath());
        final Content content3 = createContent(parentContent.getPath());

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
    public void deep_children() throws Exception{

        final Content rootContent = createContent(ContentPath.ROOT);
        final Content childrenLevel1 = createContent(rootContent.getPath());
        final Content childrenLevel2_1 = createContent(childrenLevel1.getPath());
        final Content childrenLevel2_2 = createContent(childrenLevel1.getPath());
        final Content childrenLevel2_3 = createContent(childrenLevel1.getPath());

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
    public void invalid_parent_path() throws Exception{

        final Content rootContent = createContent(ContentPath.ROOT);
        final Content childrenLevel1 = createContent(rootContent.getPath());

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
    public void params_size_zero() throws Exception{

        final Content parentContent = createContent(ContentPath.ROOT);
        final Content content1 = createContent(parentContent.getPath());
        final Content content2 = createContent(parentContent.getPath());
        final Content content3 = createContent(parentContent.getPath());

        final ContentPath parentContentPath = parentContent.getPath();

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 0 ).
            parentPath( parentContentPath ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

        assertNotNull( result );
        assertEquals( 0, result.getHits() );
        assertEquals( 3, result.getTotalHits() );
        assertTrue( result.getContents().isEmpty() );

    }

    @Test
    public void params_size_one() throws Exception{

        final Content parentContent = createContent(ContentPath.ROOT);
        final Content content1 = createContent(parentContent.getPath());
        final Content content2 = createContent(parentContent.getPath());
        final Content content3 = createContent(parentContent.getPath());

        final ContentPath parentContentPath = parentContent.getPath();

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
    public void params_from_beyond() throws Exception{

        final Content parentContent = createContent(ContentPath.ROOT);
        final Content content1 = createContent(parentContent.getPath());
        final Content content2 = createContent(parentContent.getPath());
        final Content content3 = createContent(parentContent.getPath());

        final ContentPath parentContentPath = parentContent.getPath();

        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 10 ).
            parentPath( parentContentPath ).
            build();

        final FindContentByParentResult result = contentService.findByParent( params );

        assertNotNull( result );
        assertEquals( 0, result.getHits() );
        assertEquals( 3, result.getTotalHits() );
        assertTrue( result.getContents().isEmpty() );

    }

    @Test
    public void params_from() throws Exception{

        final Content parentContent = createContent(ContentPath.ROOT);
        final Content content1 = createContent(parentContent.getPath());
        final Content content2 = createContent(parentContent.getPath());
        final Content content3 = createContent(parentContent.getPath());
        final Content content4 = createContent(parentContent.getPath());

        final ContentPath parentContentPath = parentContent.getPath();

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
}
