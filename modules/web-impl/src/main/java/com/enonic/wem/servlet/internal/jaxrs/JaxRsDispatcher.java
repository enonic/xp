package com.enonic.wem.servlet.internal.jaxrs;

import java.util.List;

import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.util.GetRestful;

import com.google.common.collect.Lists;

import com.enonic.xp.web.jaxrs.ResourceProvider;

final class JaxRsDispatcher
    extends ServletContainerDispatcher
{
    public void apply( final Application app )
    {
        final List<Object> resourceList = Lists.newArrayList();
        final List<Object> providerList = Lists.newArrayList();
        final List<ResourceFactory> factoryList = Lists.newArrayList();

        for ( final Object object : app.getSingletons() )
        {
            if ( isRootResource( object ) )
            {
                resourceList.add( object );
            }
            else if ( isResourceProvider( object ) )
            {
                factoryList.add( new JaxRsResourceFactory( (ResourceProvider) object ) );
            }
            else
            {
                providerList.add( object );
            }
        }

        providerList.forEach( this.providerFactory::registerProviderInstance );
        resourceList.forEach( this.dispatcher.getRegistry()::addSingletonResource );
        factoryList.forEach( this.dispatcher.getRegistry()::addResourceFactory );
    }

    private boolean isRootResource( final Object instance )
    {
        return GetRestful.isRootResource( instance.getClass() );
    }

    private boolean isResourceProvider( final Object instance )
    {
        return instance instanceof ResourceProvider;
    }
}
