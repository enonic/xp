package com.enonic.xp.script.impl.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

final class LogArgConverter
{
    private final ObjectMapper mapper;

    private final JsObjectConverter converter;

    LogArgConverter( final JavascriptHelper helper )
    {
        this.mapper = new ObjectMapper();
        this.mapper.disable( SerializationFeature.INDENT_OUTPUT );
        this.converter = new JsObjectConverter( helper );
    }

    Object[] convertArgs( final Object[] args )
    {
        final Object[] target = new Object[args.length];
        for ( int i = 0; i < args.length; i++ )
        {
            target[i] = convertArg( args[i] );
        }

        return target;
    }

    private Object convertArg( final Object arg )
    {
        if ( arg == null )
        {
            return null;
        }

        final Object result = this.converter.fromJs( arg );
        if ( result instanceof Map )
        {
            return toJson( result );
        }

        if ( result instanceof List )
        {
            return toJson( result );
        }

        return arg;
    }

    private Object toJson( final Object value )
    {
        try
        {
            return this.mapper.writeValueAsString( value );
        }
        catch ( final Exception e )
        {
            return value;
        }
    }
}
