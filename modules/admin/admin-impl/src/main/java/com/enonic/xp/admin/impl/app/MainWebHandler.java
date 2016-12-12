package com.enonic.xp.admin.impl.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@Component(immediate = true, service = WebHandler.class)
public final class MainWebHandler
    extends BaseWebHandler
{
    private final static Pattern ASSET_PATTERN = Pattern.compile( "/admin/(.+)" );

    private final static Pattern VERSIONED_ASSET_PATTERN = Pattern.compile( "/admin/assets/([^/]+)/(.+)" );

    private final ResourceHandler resourceHandler;

    public MainWebHandler()
    {
        super( 100 );
        this.resourceHandler = new ResourceHandler();
    }

    @Override
    protected boolean canHandle( final WebRequest req )
    {
        final String path = req.getPath();
        return path.equals( "/" ) || path.equals( "/admin" ) || path.startsWith( "/admin/" ) || ASSET_PATTERN.matcher( path ).matches();
    }

    @Override
    protected WebResponse doHandle( final WebRequest req, final WebResponse res, final WebHandlerChain chain )
        throws Exception
    {
        final String path = req.getPath();
        Matcher matcher = VERSIONED_ASSET_PATTERN.matcher( path );
        if ( matcher.matches() )
        {
            return serveAsset( matcher.group( 1 ), matcher.group( 2 ) );
        }

        matcher = ASSET_PATTERN.matcher( path );
        if ( matcher.matches() )
        {
            return serveAsset( null, matcher.group( 1 ) );
        }

        return redirectToLoginPage();
    }

    private WebResponse redirectToLoginPage()
    {
        final String uri = ServletRequestUrlHelper.createUri( "/admin/tool" );
        return WebResponse.create().
            status( HttpStatus.TEMPORARY_REDIRECT ).
            header( "Location", uri ).
            build();
    }

    private WebResponse serveAsset( final String version, final String path )
        throws Exception
    {
        return this.resourceHandler.handle( "admin/" + path, version != null );
    }

    @Reference
    public void setResourceLocator( final ResourceLocator resourceLocator )
    {
        this.resourceHandler.setResourceLocator( resourceLocator );
    }
}
