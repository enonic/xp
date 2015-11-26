package com.enonic.xp.content;

public class ContentRelativePathResolver
{
    public static String create( final Content content, final String pathExpression )
    {
        return getAbsolute( resolveActualPath( content, pathExpression ) );
    }

    private static String getAbsolute( final String resolvedPath )
    {
        return resolvedPath;
    }

    private static String resolveActualPath( final Content content, final String path )
    {
        if ( "*".equals( path ) || "/".equals( path ) || "/*".equals( path ) )
        {
            return "/*"; // any path
        }
        else if ( "./".equals( path ) || "./*".equals( path ) )
        {
            return content.getPath() + "/*"; // all children of current item
        }
        else if ( "../".equals( path ) || "../*".equals( path ) )
        {
            return content.getParentPath().isRoot()
                ? content.getParentPath() + "*"
                : content.getParentPath() + "/*"; // siblings and children of current item
        }
        else if ( path.startsWith( "../" ) )
        {
            return makeEndWithStar( getPathStartedSomeLevelsHigher( content, path ) ); // path starting x levels higher
        }
        else
        {
            return makeEndWithStar( makeStartWithSlash( path ) );
        }
    }

    private static String getPathStartedSomeLevelsHigher( final Content content, final String path )
    {
        int levels = getNumberOfLevelsToAscend( path );
        ContentPath contentPath = content.getPath();
        for ( int level = 1; level <= levels; level++ )
        {
            contentPath = contentPath.getParentPath();
            if ( contentPath.isRoot() )
            {
                return path.substring( levels * 3 );
            }
        }
        return contentPath.toString() + "/" + path.substring( levels * 3 );
    }

    private static int getNumberOfLevelsToAscend( final String path )
    {
        if ( path.startsWith( "../" ) )
        {
            return getNumberOfLevelsToAscend( path.substring( 3, path.length() ) ) + 1;
        }
        return 0;
    }

    private static String makeStartWithSlash( final String str )
    {
        if ( str.startsWith( "/" ) )
        {
            return str;
        }
        else
        {
            return "/" + str;
        }
    }

    private static String makeEndWithStar( final String str )
    {
        if ( str.endsWith( "*" ) )
        {
            return str;
        }
        else
        {
            return str + "*";
        }
    }

}
