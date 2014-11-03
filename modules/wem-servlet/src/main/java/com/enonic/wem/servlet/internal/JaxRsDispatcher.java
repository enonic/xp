package com.enonic.wem.servlet.internal;

import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher;

final class JaxRsDispatcher
    extends ServletContainerDispatcher
{
    public void addApplication( final Application app )
    {
        processApplication( app );
    }
}
