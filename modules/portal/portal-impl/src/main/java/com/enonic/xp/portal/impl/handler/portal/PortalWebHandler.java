package com.enonic.xp.portal.impl.handler.portal;

import com.google.common.base.Strings;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

public class PortalWebHandler
    extends BaseWebHandler
{
    private final static String BASE_URI = "/portal";

    private final static String BRANCH_PREFIX = BASE_URI + "/";

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getPath().startsWith( "/portal/" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final PortalWebRequest portalWebRequest;
        if ( webRequest instanceof PortalWebRequest )
        {
            portalWebRequest = (PortalWebRequest) webRequest;
        }
        else
        {
            final String path = webRequest.getPath();
            final Branch branch = findBranch( path );
            final ContentPath contentPath = findContentPath( path );

            portalWebRequest = PortalWebRequest.create( webRequest ).
                baseUri( BASE_URI ).
                branch( branch ).
                contentPath( contentPath ).
//                site( site ).
//                content( content ).
//                pageTemplate().
//                component().
//                applicationKey().
//                pageDescriptor().
//                controllerScript().
    build();
        }
        return webHandlerChain.handle( portalWebRequest, new PortalWebResponse() );
    }

    private static Branch findBranch( final String path )
    {
        final int index = path.indexOf( '/', BRANCH_PREFIX.length() );
        final String result = path.substring( BRANCH_PREFIX.length(), index > 0 ? index : path.length() );
        return Strings.isNullOrEmpty( result ) ? null : Branch.from( result );
    }

    private static ContentPath findContentPath( final String path )
    {
        final String restPath = findPathAfterBranch( path );
        final int underscore = restPath.indexOf( "/_/" );
        final String result = restPath.substring( 0, underscore > -1 ? underscore : restPath.length() );
        return ContentPath.from( result.startsWith( "/" ) ? result : ( "/" + result ) );
    }

    private static String findPathAfterBranch( final String path )
    {
        final int index = path.indexOf( '/', BRANCH_PREFIX.length() );
        return path.substring( index > 0 ? index : path.length() );
    }
}
