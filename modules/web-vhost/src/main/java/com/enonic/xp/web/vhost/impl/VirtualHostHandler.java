package com.enonic.xp.web.vhost.impl;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.web.WebContext;
import com.enonic.xp.web.WebHandler;

@Component(immediate = true)
public final class VirtualHostHandler
    implements WebHandler
{
    private final static Logger LOG = LoggerFactory.getLogger( VirtualHostHandler.class );

    @Override
    public int getOrder()
    {
        return MIN_ORDER + 10;
    }

    @Override
    public boolean handle( final WebContext context )
        throws Exception
    {
        LOG.info( "Executing virtual-host handler for " + context.getPath() );
        return false;
    }
}
