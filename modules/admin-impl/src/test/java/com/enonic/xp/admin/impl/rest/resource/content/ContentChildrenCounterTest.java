package com.enonic.xp.admin.impl.rest.resource.content;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentByQueryResult;

import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentChildrenCounterTest
{
    private ContentService contentService;

    private ContentChildrenCounter contentChildrenCounter;

    @Before
    public void init()
    {
        this.contentService = mock( ContentService.class );
        this.contentChildrenCounter = new ContentChildrenCounter( this.contentService );

        when( this.contentService.find( any() ) ).thenReturn( FindContentByQueryResult.create().totalHits( 0L ).build() );
    }

    @Test
    public void count_contents_with_no_children()
    {
        assertEquals( 3L, contentChildrenCounter.countContentsAndTheirChildren( ContentPaths.from( "/root/a", "/root/b", "/root/c" ) ) );
    }

    @Test
    public void no_content_paths()
    {
        assertEquals( 0L, contentChildrenCounter.countContentsAndTheirChildren( ContentPaths.empty() ) );
    }
}
