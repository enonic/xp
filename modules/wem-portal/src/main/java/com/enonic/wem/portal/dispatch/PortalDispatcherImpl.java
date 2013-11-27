package com.enonic.wem.portal.dispatch;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Splitter;

@Singleton
public final class PortalDispatcherImpl
    implements PortalDispatcher
{
    @Override
    public void dispatch( final HttpServletRequest req, final HttpServletResponse res )
        throws IOException
    {
        try
        {
            doDispatch( req, res );
        }
        catch ( final Exception e )
        {
            handleError( req, res, e );
        }
    }

    private void doDispatch( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final String path = req.getPathInfo();
        final Iterable<String> pathElements = Splitter.on( '/' ).omitEmptyStrings().trimResults().split( path );



        // /{workspace}/{mode}/{site}

    }

    private void handleError( final HttpServletRequest req, final HttpServletResponse res, final Exception error )
        throws IOException
    {

    }
}
