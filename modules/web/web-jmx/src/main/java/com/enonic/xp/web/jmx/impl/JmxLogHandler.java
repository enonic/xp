package com.enonic.xp.web.jmx.impl;

import org.jolokia.util.LogHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JmxLogHandler
    implements LogHandler
{
    private final static Logger LOG = LoggerFactory.getLogger( JmxServlet.class );

    @Override
    public void debug( final String message )
    {
        LOG.debug( message );
    }

    @Override
    public void info( final String message )
    {
        LOG.info( message );
    }

    @Override
    public void error( final String message, final Throwable t )
    {
        LOG.error( message, t );
    }
}
