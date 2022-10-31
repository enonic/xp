package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ResolveRequiredDependenciesParams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentServiceImplTest_resolveRequiredDependencies
    extends AbstractContentServiceTest
{
    private Content content1, content2, content3;

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
            build() );

        assertEquals( 0, result.getSize() );
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
            build() );

        assertEquals( 0, result.getSize() );
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
            build() );

        assertEquals( 2, result.getSize() );
        assertTrue( result.contains( content1.getId() ) );
        assertTrue( result.contains( content2.getId() ) );
    }


}
