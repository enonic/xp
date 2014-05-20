package com.enonic.wem.core.web.jaxrs;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

final class JaxRsResourceConfig
    extends DefaultResourceConfig
{
    public void addClass( final Class<?> type )
    {
        if ( ResourceConfig.isProviderClass( type ) || ResourceConfig.isRootResourceClass( type ) )
        {
            getClasses().add( type );
        }
    }

    public void addSingleton( final Object instance )
    {
        getSingletons().add( instance );
    }

    public void setFeature( final String name, final boolean flag )
    {
        getFeatures().put( name, flag );
    }

    public void setProperty( final String name, final Object value )
    {
        getProperties().put( name, value );
    }
}
