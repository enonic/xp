package com.enonic.wem.core.content;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
}
