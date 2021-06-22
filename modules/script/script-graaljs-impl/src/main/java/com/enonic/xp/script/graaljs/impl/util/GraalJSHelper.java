package com.enonic.xp.script.graaljs.impl.util;

import java.util.Date;

import org.graalvm.polyglot.Value;

public final class GraalJSHelper
{
    public static void addToNativeObject( final Value object, final String key, final Object value )
    {
        object.putMember( key, value );
    }

    public static void addToNativeArray( final Object array, final Object value )
    {
        Value.asValue( array ).getMember( "push" ).execute( value );
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
}
