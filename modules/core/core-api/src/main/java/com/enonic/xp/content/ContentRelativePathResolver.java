package com.enonic.xp.content;

import java.util.List;

import com.enonic.xp.site.Site;

public class ContentRelativePathResolver
{

    public static final String SITE_WILDCARD = "${site}";

    public static String resolve( final Content content, final String pathExpression )
    {
        return getAbsolute( resolveActualPath( content, pathExpression ) );
    }

    public static String resolveWithSite( final String pathExpression, final Site parentSite )
    {
        return getAbsolute( makeEndWithStar( makeStartWithSlash( resolveSitePath( pathExpression, parentSite ) ) ) );
    }

    public static boolean anyPathNeedsSiteResolving( final List<String> allowedPaths )
    {
        return allowedPaths.stream().anyMatch( ( s ) -> s.startsWith( SITE_WILDCARD ) );
    }

    public static boolean hasSiteToResolve( final String path )
    {
        return path.startsWith( ContentRelativePathResolver.SITE_WILDCARD );
    }

    private static String resolveSitePath( final String pathExpression, final Site site )
    {
        return site != null ? pathExpression.replace( SITE_WILDCARD, site.getPath().toString() ) : pathExpression;
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
            return makeEndWithStar(
                makeStartWithSlash( getPathStartedSomeLevelsHigher( content, path ) ) ); // path starting x levels higher
        }
        else if ( path.startsWith( "./" ) )
        {
            return makeEndWithStar( makeStartWithSlash( getChildPath( content, path ) ) ); // child path for current item
        }
        else
        {
            return makeEndWithStar( makeStartWithSlash( path ) );
        }
    }

    private static String getChildPath( final Content content, final String path )
    {
        return content.getPath() + path.substring( 1 );
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
            return getNumberOfLevelsToAscend( path.substring( 3 ) ) + 1;
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
