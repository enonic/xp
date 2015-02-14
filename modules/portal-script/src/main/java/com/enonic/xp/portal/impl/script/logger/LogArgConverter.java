package com.enonic.xp.portal.impl.script.logger;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.util.Exceptions;
import com.enonic.xp.portal.impl.script.bean.JsObjectConverter;

final class LogArgConverter
{
    private final ObjectMapper mapper;

    public LogArgConverter()
    {
        this.mapper = new ObjectMapper();
        this.mapper.disable( SerializationFeature.INDENT_OUTPUT );
    }

    public Object[] convertArgs( final Object[] args )
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

        final Object result = JsObjectConverter.fromJs( arg );
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

    private String toJson( final Object value )
    {
        try
        {
            return this.mapper.writeValueAsString( value );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}
