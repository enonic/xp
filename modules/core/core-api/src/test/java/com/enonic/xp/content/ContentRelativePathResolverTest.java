package com.enonic.xp.content;

import org.junit.Test;

import static org.junit.Assert.*;

public class ContentRelativePathResolverTest
{

    @Test
    public void any_path_wildcard()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myPath" ) ).
            name( "test" ).
            build();

        final String resolvedWildcardOption1 = ContentRelativePathResolver.create( content, "/" );
        final String resolvedWildcardOption2 = ContentRelativePathResolver.create( content, "/*" );
        final String resolvedWildcardOption3 = ContentRelativePathResolver.create( content, "*" );

        assertEquals( "/*", resolvedWildcardOption1 );
        assertEquals( "/*", resolvedWildcardOption2 );
        assertEquals( "/*", resolvedWildcardOption3 );
    }

    @Test
    public void siblings_and_current()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myPath" ) ).
            name( "test" ).
            build();

        final String resolvedWildcardOption1 = ContentRelativePathResolver.create( content, "../" );
        final String resolvedWildcardOption2 = ContentRelativePathResolver.create( content, "../*" );

        assertEquals( "/myPath/*", resolvedWildcardOption1 );
        assertEquals( "/myPath/*", resolvedWildcardOption2 );
    }

    @Test
    public void children_of_current_wildcard()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myPath" ) ).
            name( "test" ).
            build();

        final String resolvedWildcardOption1 = ContentRelativePathResolver.create( content, "./" );
        final String resolvedWildcardOption2 = ContentRelativePathResolver.create( content, "./*" );

        assertEquals( "/myPath/test/*", resolvedWildcardOption1 );
        assertEquals( "/myPath/test/*", resolvedWildcardOption2 );
    }

    @Test
    public void children_of_current_wildcard_with_child_path()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myPath" ) ).
            name( "test" ).
            build();

        final String resolved1 = ContentRelativePathResolver.create( content, "./images" );
        final String resolved2 = ContentRelativePathResolver.create( content, "./images/" );

        assertEquals( "/myPath/test/images*", resolved1 );
        assertEquals( "/myPath/test/images/*", resolved2 );
    }

    @Test
    public void regular_path()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myPath" ) ).
            name( "test" ).
            build();

        final String resolved = ContentRelativePathResolver.create( content, "some-path1/some-path2/images" );

        assertEquals( "/some-path1/some-path2/images*", resolved );
    }

    @Test
    public void parent_path_one_level_up()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myParentPath1/myParentPath2" ) ).
            name( "test" ).
            build();

        final String resolved = ContentRelativePathResolver.create( content, "../images/" );

        assertEquals( "/myParentPath1/myParentPath2/images/*", resolved );
    }

    @Test
    public void parent_path_two_levels_up()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myParentPath1/myParentPath2" ) ).
            name( "test" ).
            build();

        final String resolved = ContentRelativePathResolver.create( content, "../../images" );

        assertEquals( "/myParentPath1/images*", resolved );
    }

    @Test
    public void parent_path_up_to_root()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myParentPath1/myParentPath2" ) ).
            name( "test" ).
            build();

        final String resolved = ContentRelativePathResolver.create( content, "../../../images/" );

        assertEquals( "/images/*", resolved );
    }
}