package com.enonic.xp.content;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class ContentRelativePathResolverTest
{

    @Test
    public void siblings_and_current()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myPath" ) ).
            name( "test" ).
            build();

        final String resolved = ContentRelativePathResolver.create( content, "../" );

        assertEquals( "/myPath/*", resolved );
    }

    @Ignore(( "Should work?" ))
    @Test
    public void children_of_current_wildcard()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myPath" ) ).
            name( "test" ).
            build();

        final String resolved = ContentRelativePathResolver.create( content, "./images" );

        assertEquals( "/myPath/test/images*", resolved );
    }

    @Test
    public void child_wildcard()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myPath" ) ).
            name( "test" ).
            build();

        final String resolved = ContentRelativePathResolver.create( content, "../images/" );

        assertEquals( "/myPath/images/*", resolved );
    }

    @Ignore("Should work?")
    @Test
    public void parent_path()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myPath" ) ).
            name( "test" ).
            build();

        final String resolved = ContentRelativePathResolver.create( content, "../../images/" );

        assertEquals( "/myPath/images/*", resolved );
    }


}