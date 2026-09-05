package com.enonic.xp.script.graal.util;

import java.util.Date;

import org.graalvm.polyglot.Value;

public final class GraalJSHelper
{
    private static final String PROTO_KEY = "__proto__";

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

    public static boolean isNativeObject( final Object value )
    {
        if ( value instanceof Value )
        {
            Value copyValue = (Value) value;
            return !( copyValue.isNull() || copyValue.isBoolean() || copyValue.isString() || copyValue.isDate() || copyValue.isNumber() ||
                copyValue.isHostObject() || copyValue.hasArrayElements() );
        }
        return false;
    }

    public static void addToNativeObject( final Object object, final String key, final Object value )
    {
        final Value target = Value.asValue( object );
        if ( PROTO_KEY.equals( key ) )
        {
            defineOwnProperty( target, key, value );
        }
        else
        {
            target.putMember( key, value );
        }
    }

    private static void defineOwnProperty( final Value target, final String key, final Object value )
    {
        final Value objectConstructor = target.getContext().getBindings( "js" ).getMember( "Object" );
        final Value descriptor = objectConstructor.newInstance();
        descriptor.putMember( "value", value );
        descriptor.putMember( "writable", true );
        descriptor.putMember( "enumerable", true );
        descriptor.putMember( "configurable", true );
        objectConstructor.getMember( "defineProperty" ).execute( target, key, descriptor );
    }

    public static void addToNativeArray( final Object array, final Object value )
    {
        Value.asValue( array ).getMember( "push" ).execute( value );
    }
}
