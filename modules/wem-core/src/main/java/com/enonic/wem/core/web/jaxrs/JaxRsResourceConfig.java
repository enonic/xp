package com.enonic.wem.core.web.jaxrs;

import com.sun.jersey.api.core.DefaultResourceConfig;

final class JaxRsResourceConfig
    extends DefaultResourceConfig
{
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
