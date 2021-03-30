package com.enonic.xp.script.graal.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.enonic.xp.script.impl.util.JavascriptHelper;

public final class GraalJavascriptHelperFactory
{
    public JavascriptHelper<Value> create( final Context context )
    {
        return new JavascriptHelper<>()
        {
            @Override
            public Value newJsArray()
            {
                synchronized ( context )
                {
                    return context.getBindings( "js" ).getMember( "Array" ).newInstance();
                }
            }

            @Override
            public Value newJsObject()
            {
                synchronized ( context )
                {
                    return context.getBindings( "js" ).getMember( "Object" ).newInstance();
                }
            }

            @Override
            public Value parseJson( final String text )
            {
                synchronized ( context )
                {
                    return context.getBindings( "js" ).getMember( "JSON" ).getMember( "parse" ).execute( text );
                }
            }

            @Override
            public Value eval( final String script )
            {
                synchronized ( context )
                {
                    return context.eval( "js", script );
                }
            }

            @Override
            public GraalObjectConverter objectConverter() {
                return new GraalObjectConverter( this );
            }
        };
    }
}
