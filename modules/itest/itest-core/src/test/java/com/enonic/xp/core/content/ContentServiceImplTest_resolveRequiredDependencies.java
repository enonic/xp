package com.enonic.xp.core.content;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ResolveRequiredDependenciesParams;

import static org.junit.Assert.*;

public class ContentServiceImplTest_resolveRequiredDependencies
    extends AbstractContentServiceTest
{
    private Content content1, content2, content3;

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void resolve_empty()
        throws Exception
    {
        content1 = createContent( ContentPath.ROOT );
        content2 = createContent( content1.getPath() );
        content3 = createContent( content2.getPath() );

        refresh();

        final ContentIds result = this.contentService.resolveRequiredDependencies( ResolveRequiredDependenciesParams.create().
            contentIds( ContentIds.empty() ).
            target( WS_OTHER ).
            build() );

        assertTrue( result.getSize() == 0 );
    }

    @Test
    public void resolve_with_no_parent()
        throws Exception
    {
        content1 = createContent( ContentPath.ROOT );
        content2 = createContent( ContentPath.ROOT );
        content3 = createContent( ContentPath.ROOT );

        refresh();

        final ContentIds result = this.contentService.resolveRequiredDependencies( ResolveRequiredDependenciesParams.create().
            contentIds( ContentIds.from( content1.getId(), content2.getId(), content3.getId() ) ).
            target( WS_OTHER ).
            build() );

        assertTrue( result.getSize() == 0 );
    }

    @Test
    public void resolve_with_parent()
        throws Exception
    {
        Content content1 = createContent( ContentPath.ROOT );
        Content content2 = createContent( content1.getPath() );

        refresh();

        final ContentIds result = this.contentService.resolveRequiredDependencies( ResolveRequiredDependenciesParams.create().
            contentIds( ContentIds.from( content1.getId(), content2.getId() ) ).
            target( WS_OTHER ).
            build() );

        assertEquals( content1.getId(), result.first() );
    }

    @Test
    public void resolve_hierarchy()
        throws Exception
    {
        Content content1 = createContent( ContentPath.ROOT );
        Content content2 = createContent( content1.getPath() );
        Content content3 = createContent( content2.getPath() );

        refresh();

        final ContentIds result = this.contentService.resolveRequiredDependencies( ResolveRequiredDependenciesParams.create().
            contentIds( ContentIds.from( content1.getId(), content2.getId(), content3.getId() ) ).
            target( WS_OTHER ).
            build() );

        assertTrue( result.getSize() == 2 );
        assertTrue( result.contains( content1.getId() ) );
        assertTrue( result.contains( content2.getId() ) );
    }


}
