package com.enonic.wem.core.content;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;

import static org.junit.Assert.*;

public class ContentToNodeNameResolverTest
{

    @Test
    public void resolve_when_path_has_elements_the_path_is_used()
        throws Exception
    {
        final Content test = Content.newContent().name( "content" ).path( ContentPath.from( "/my/test" ) ).build();

        final String resolvedPath = ContentToNodeNameResolver.resolve( test );

        assertEquals( "my/test", resolvedPath );
    }

    @Test
    public void resolve_when_path_has_no_elements_the_name_is_used()
        throws Exception
    {
        final Content test = Content.newContent().
            parentPath( ContentPath.ROOT ).
            name( "content" ).
            build();

        final String resolvedPath = ContentToNodeNameResolver.resolve( test );
        assertEquals( "content", resolvedPath );
    }

}
