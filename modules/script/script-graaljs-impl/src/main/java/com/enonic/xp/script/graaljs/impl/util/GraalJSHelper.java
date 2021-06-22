package com.enonic.xp.script.graaljs.impl.util;

import java.util.Date;

import org.graalvm.polyglot.Value;

public final class GraalJSHelper
{
    public static boolean isUndefined( final Object value )
    {
        return value == null || Value.asValue( value ).isNull();
    }

    public static boolean isNativeArray( final Object value )
    {
        return ( value instanceof Value ) && ( (Value) value ).hasArrayElements();
    }

    public static boolean isDateType( final Value value )
    {
        return "Date".equalsIgnoreCase( value.getMetaObject().getMetaSimpleName() );
    }

    public static Date toDate( final Value value )
    {
        long time = value.getMember( "getTime" ).execute().asLong();
        return new Date( time );
    }

    static boolean isNativeObject( final Object value )
    {
        if ( value instanceof Value )
        {
            Value copyValue = (Value) value;
            return !( copyValue.isNull() || copyValue.isBoolean() || copyValue.isString() || copyValue.isDate() || copyValue.isNumber() ||
                copyValue.isHostObject() || copyValue.hasArrayElements() );
        }
        return false;
    }

    static void addToNativeObject( final Object object, final String key, final Object value )
    {
        Value.asValue( object ).putMember( key, value );
    }

    static void addToNativeArray( final Object array, final Object value )
    {
        Value.asValue( array ).getMember( "push" ).execute( value );
    }
}
