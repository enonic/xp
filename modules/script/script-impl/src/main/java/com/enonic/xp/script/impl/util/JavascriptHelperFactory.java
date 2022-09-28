package com.enonic.xp.script.impl.util;

import java.util.function.Function;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.JSObject;

public final class JavascriptHelperFactory
{
    private final ScriptEngine engine;

    public JavascriptHelperFactory( final ScriptEngine engine )
    {
        this.engine = engine;
    }

    public JavascriptHelper<Bindings> create()
    {
        final Bindings bindings = this.engine.getBindings( ScriptContext.ENGINE_SCOPE );
        final JSObject arrayProto = (JSObject) bindings.get( "Array" );
        final JSObject objectProto = (JSObject) bindings.get( "Object" );
        final JSObject jsonProto = (JSObject) bindings.get( "JSON" );

        return new JavascriptHelper<>()
        {
            @Override
            public Bindings newJsArray()
            {
                return (Bindings) arrayProto.newObject();
            }

            @Override
            public Bindings newJsObject()
            {
                return (Bindings) objectProto.newObject();
            }

            @Override
            public Object newFunction( final Function<?, ?> function )
            {
                try
                {
                    return ( (JSObject) engine.eval( "f => a => f.apply(a)" ) ).call( null, function );
                }
                catch ( ScriptException e )
                {
                    throw new RuntimeException( e );
                }
            }

            @Override
            public Bindings parseJson( final String text )
            {
                return (Bindings) ( (JSObject) jsonProto.getMember( "parse" ) ).call( null, text );
            }

            @Override
            public Object eval( final String script )
            {
                try
                {
                    return engine.eval( script );
                }
                catch ( ScriptException e )
                {
                    throw new RuntimeException( e );
                }
            }

            @Override
            public JsObjectConverter objectConverter()
            {
                return new JsObjectConverter( this );
            }
        };
    }
}
