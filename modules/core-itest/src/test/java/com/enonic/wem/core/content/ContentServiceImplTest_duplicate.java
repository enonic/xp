package com.enonic.wem.core.content;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DuplicateContentParams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ContentServiceImplTest_duplicate extends AbstractContentServiceTest {
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void root_content() throws Exception {
        final Content rootContent = createContent( ContentPath.ROOT);
        final DuplicateContentParams params = new DuplicateContentParams( rootContent.getId() );
        final Content duplicatedContent = contentService.duplicate( params );

        assertNotNull( duplicatedContent );
        assertEquals( rootContent.getDisplayName(), duplicatedContent.getDisplayName() );
        assertEquals( rootContent.getParentPath(), duplicatedContent.getParentPath());
        assertEquals( rootContent.getPath().toString() + "-copy", duplicatedContent.getPath().toString());
    }

    @Test
    public void deep_children() throws Exception {
        final Content rootContent = createContent(ContentPath.ROOT);
        final Content childrenLevel1 = createContent(rootContent.getPath());
        final Content childrenLevel2 = createContent(childrenLevel1.getPath());
        final DuplicateContentParams params = new DuplicateContentParams( childrenLevel2.getId() );
        final Content duplicatedContent = contentService.duplicate( params );

        assertNotNull( duplicatedContent );
        assertEquals( childrenLevel2.getDisplayName(), duplicatedContent.getDisplayName() );
        assertEquals( childrenLevel2.getParentPath(), duplicatedContent.getParentPath());
        assertEquals( childrenLevel2.getPath().toString() + "-copy", duplicatedContent.getPath().toString());
    }

}
