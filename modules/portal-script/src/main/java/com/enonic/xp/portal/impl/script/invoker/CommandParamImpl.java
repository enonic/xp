package com.enonic.xp.portal.impl.script.invoker;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;

import com.enonic.xp.core.convert.Converters;
import com.enonic.xp.portal.script.command.CommandParam;
import com.enonic.xp.portal.impl.script.bean.JsObjectConverter;

final class CommandParamImpl
    implements CommandParam
{
    private final String name;

    private final Object value;

    private boolean required = false;

    public CommandParamImpl( final String name, final Object value )
    {
        this.name = name;
        this.value = value;
    }

    @Override
    public CommandParam required()
    {
        this.required = true;
        return this;
    }

    private void checkRequired()
    {
        if ( this.required && ( this.value == null ) )
        {
            throw new IllegalArgumentException( String.format( "Parameter [%s] is required", this.name ) );
        }
    }

    @Override
    public <T> T value( final Class<T> type )
    {
        return value( type, null );
    }

    @Override
    public <T> T value( final Class<T> type, final T defValue )
    {
        checkRequired();

        if ( this.value == null )
        {
            return defValue;
        }

        return Converters.convert( this.value, type );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Function<Object[], Object> callback()
    {
        checkRequired();

        if ( this.value instanceof Function )
        {
            return wrapFunction( (Function<Object[], Object>) this.value );
        }

        return null;
    }

    private Function<Object[], Object> wrapFunction( final Function<Object[], Object> func )
    {
        return new Function<Object[], Object>()
        {
            @Override
            public Object apply( final Object[] args )
            {
                return func.apply( JsObjectConverter.toJsArray( args ) );
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> map()
    {
        checkRequired();

        if ( this.value instanceof Map )
        {
            return (Map<String, Object>) this.value;
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> array( final Class<T> type )
    {
        checkRequired();

        if ( this.value instanceof List )
        {
            return convertArray( (List<Object>) this.value, type );
        }

        return Lists.newArrayList();
    }

    private <T> List<T> convertArray( final List<Object> list, final Class<T> type )
    {
        final List<T> result = Lists.newArrayList();
        for ( final Object value : list )
        {
            result.add( Converters.convert( value, type ) );
        }

        return result;
    }
}
