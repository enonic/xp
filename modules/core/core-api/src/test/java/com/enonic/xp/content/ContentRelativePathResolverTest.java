package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.site.Site;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentRelativePathResolverTest
{

    @Test
    public void site_wildcard()
        throws Exception
    {

        final Site parentSite = Site.create().
            parentPath( ContentPath.from( "/mySitePath" ) ).
            name( "test" ).
            build();

        final String resolvedWildcardOption1 =
            ContentRelativePathResolver.resolveWithSite( ContentRelativePathResolver.SITE_WILDCARD, parentSite );
        final String resolvedWildcardOption2 =
            ContentRelativePathResolver.resolveWithSite( ContentRelativePathResolver.SITE_WILDCARD + "/somepath/", parentSite );

        assertEquals( "/mySitePath/test*", resolvedWildcardOption1 );
        assertEquals( "/mySitePath/test/somepath/*", resolvedWildcardOption2 );
    }

    @Test
    public void unresolved_site_wildcard()
        throws Exception
    {

        final String resolvedWildcardOption1 =
            ContentRelativePathResolver.resolveWithSite( ContentRelativePathResolver.SITE_WILDCARD + "/somePath", null );

        assertEquals( "/${site}/somePath*", resolvedWildcardOption1 );
    }

    @Test
    public void any_path_wildcard()
        throws Exception
    {
        final Content content = Content.create().
            parentPath( ContentPath.from( "/myPath" ) ).
            name( "test" ).
            build();

        final String resolvedWildcardOption1 = ContentRelativePathResolver.resolve( content, "/" );
        final String resolvedWildcardOption2 = ContentRelativePathResolver.resolve( content, "/*" );
        final String resolvedWildcardOption3 = ContentRelativePathResolver.resolve( content, "*" );

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

        final String resolvedWildcardOption1 = ContentRelativePathResolver.resolve( content, "../" );
        final String resolvedWildcardOption2 = ContentRelativePathResolver.resolve( content, "../*" );

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

        final String resolvedWildcardOption1 = ContentRelativePathResolver.resolve( content, "./" );
        final String resolvedWildcardOption2 = ContentRelativePathResolver.resolve( content, "./*" );

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

        final String resolved1 = ContentRelativePathResolver.resolve( content, "./images" );
        final String resolved2 = ContentRelativePathResolver.resolve( content, "./images/" );

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

        final String resolved = ContentRelativePathResolver.resolve( content, "some-path1/some-path2/images" );

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

        final String resolved = ContentRelativePathResolver.resolve( content, "../images/" );

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

        final String resolved = ContentRelativePathResolver.resolve( content, "../../images" );

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

        final String resolved = ContentRelativePathResolver.resolve( content, "../../../images/" );

        assertEquals( "/images/*", resolved );
    }
}
