package com.enonic.xp.portal.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class PortalDispatcher
    extends BaseWebHandler
{
    private final static Pattern PATTERN = Pattern.compile( "/portal2/([^/]+)(/.*)?" );

    public PortalDispatcher()
    {
        setOrder( MAX_ORDER - 30 );
    }

    @Override
    protected boolean canHandle( final HttpServletRequest req )
    {
        return PATTERN.matcher( req.getPathInfo() ).matches();
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        final Matcher matcher = PATTERN.matcher( req.getPathInfo() );
        if ( !matcher.matches() )
        {
            return;
        }

        final String branch = matcher.group( 1 );
        final String localPath = matcher.group( 2 );

        res.getWriter().println( "Here " + branch + ", " + localPath );
    }
}
