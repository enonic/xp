package com.enonic.xp.script.impl.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public final class JavascriptHelperFactory
{
    private final Context context;

    public JavascriptHelperFactory( final Context context )
    {
        this.context = context;
    }

    public JavascriptHelper create()
    {
        Value bindings = this.context.getBindings( "js" );
        Value arrayProto = bindings.getMember( "Array" );
        Value objectProto = bindings.getMember( "Object" );
        Value jsonProto = bindings.getMember( "JSON" );

        return new JavascriptHelper()
        {
            @Override
            public Value newJsArray()
            {
                return arrayProto.newInstance();
            }

            @Override
            public Value newJsObject()
            {
                return objectProto.newInstance();
            }

            @Override
            public Value parseJson( final String text )
            {
                return jsonProto.getMember( "parse" ).execute( text );
            }
        };
    }
}
