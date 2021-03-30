package com.enonic.xp.script.impl.function;

import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.app.Application;
import com.enonic.xp.config.Configuration;

public final class ApplicationInfoBuilder
{
    private Application application;

    private Supplier<Map<String, Object>> mapSupplier;

    public ApplicationInfoBuilder application( final Application application )
    {
        this.application = application;
        return this;
    }

    public ApplicationInfoBuilder mapSupplier( final Supplier<Map<String, Object>> mapSupplier )
    {
        this.mapSupplier = mapSupplier;
        return this;
    }

    public Map<String, Object> build()
    {
        final Map<String, Object> result = mapSupplier.get();
        result.put( "name", toString( this.application.getKey() ) );
        result.put( "version", toString( this.application.getVersion() ) );
        result.put( "config", buildConfig() );
        return result;
    }

    private Map<String, Object> buildConfig()
    {
        final Map<String, Object> result = mapSupplier.get();
        final Configuration config = this.application.getConfig();

        if ( config != null )
        {
            result.putAll( config.asMap() );
        }

        return result;
    }

    private String toString( final Object value )
    {
        return value != null ? value.toString() : "";
    }
}
