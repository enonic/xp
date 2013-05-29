package com.enonic.wem.web.jaxrs;

import java.util.List;

import com.google.common.collect.Lists;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

final class JaxRsResourceConfig
    extends DefaultResourceConfig
{
    private final List<String> requestFilters;

    private final List<String> responseFilters;

    private final List<String> resourceFilterFactories;

    public JaxRsResourceConfig()
    {
        this.requestFilters = Lists.newArrayList();
        this.responseFilters = Lists.newArrayList();
        this.resourceFilterFactories = Lists.newArrayList();
    }

    public void addClass( final Class<?> type )
    {
        if ( isRequestFilter( type ) )
        {
            this.requestFilters.add( type.getName() );
        }

        if ( isResponseFilter( type ) )
        {
            this.responseFilters.add( type.getName() );
        }

        if ( isResourceFilterFactory( type ) )
        {
            this.resourceFilterFactories.add( type.getName() );
        }

        if ( isProviderClass( type ) || isRootResourceClass( type ) )
        {
            getClasses().add( type );
        }
    }

    private boolean isRequestFilter( final Class<?> type )
    {
        return ContainerRequestFilter.class.isAssignableFrom( type );
    }

    private boolean isResponseFilter( final Class<?> type )
    {
        return ContainerResponseFilter.class.isAssignableFrom( type );
    }

    private boolean isResourceFilterFactory( final Class<?> type )
    {
        return ResourceFilterFactory.class.isAssignableFrom( type );
    }

    @Override
    public List getContainerRequestFilters()
    {
        return this.requestFilters;
    }

    @Override
    public List getContainerResponseFilters()
    {
        return this.responseFilters;
    }

    @Override
    public List getResourceFilterFactories()
    {
        return this.resourceFilterFactories;
    }

    public void setFeature( final String name, final boolean flag )
    {
        getFeatures().put( name, flag );
    }

    public void setProperty( final String name, final Object value )
    {
        getProperties().put( name, value );
    }

    public void addSingleton( final Object type )
    {
        getSingletons().add( type );
    }
}
