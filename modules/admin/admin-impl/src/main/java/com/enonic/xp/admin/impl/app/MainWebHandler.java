package com.enonic.xp.admin.impl.app;

import org.osgi.service.component.annotations.Component;

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
    public MainWebHandler()
    {
        super( 100 );
    }

    @Override
    protected boolean canHandle( final WebRequest req )
    {
        final String path = req.getRawPath();
        return path.equals( "" ) || path.equals( "/" ) || path.equals( "/admin" ) || path.equals( "/admin/" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest req, final WebResponse res, final WebHandlerChain chain )
        throws Exception
    {
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
}
