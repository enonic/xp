package com.enonic.xp.script.impl.function;

import java.util.Map;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.util.Version;

@NullMarked
public record ApplicationInfoBuilder(ApplicationKey appKey, Configuration appConfig, Version appVersion)
{
    public Map<String, Object> buildMap( final Supplier<Map<String, Object>> mapSupplier )
    {
        final Map<String, Object> result = mapSupplier.get();
        result.put( "name", appKey.toString() );
        result.put( "version", appVersion.toString() );
        result.put( "config", buildConfig( mapSupplier ) );
        return result;
    }

    private Map<String, Object> buildConfig( final Supplier<Map<String, Object>> mapSupplier )
    {
        final Map<String, Object> result = mapSupplier.get();
        result.putAll( appConfig.asMap() );
        return result;
    }
}
