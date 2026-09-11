package com.enonic.xp.script.impl.util;

import java.util.function.Function;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

public final class JavascriptHelperFactory
{
    private static final String PROTO_KEY = "__proto__";

    private static final String DEFINE_DATA_PROPERTY =
        "(function (object, key, value) { Object.defineProperty(object, key, {value: value, writable: true, enumerable: true, configurable: true}); })";

    private final ScriptEngine engine;

    public JavascriptHelperFactory( final ScriptEngine engine )
    {
        this.engine = engine;
    }

    private static Object eval( final ScriptEngine engine, final String script )
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

    public JavascriptHelper<Bindings> create()
    {
        final Bindings bindings = this.engine.getBindings( ScriptContext.ENGINE_SCOPE );
        final JSObject arrayProto = (JSObject) bindings.get( "Array" );
        final JSObject objectProto = (JSObject) bindings.get( "Object" );
        final JSObject jsonProto = (JSObject) bindings.get( "JSON" );
        final JSObject defineDataProperty = (JSObject) eval( this.engine, DEFINE_DATA_PROPERTY );

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
            public void defineDataProperty( final Object object, final String key, final Object value )
            {
                if ( PROTO_KEY.equals( key ) )
                {
                    defineDataProperty.call( null, object, key, value );
                }
                else
                {
                    ( (ScriptObjectMirror) object ).put( key, value );
                }
            }

            @Override
            public Object newFunction( final Function<?, ?> function )
            {
                return ( (JSObject) JavascriptHelperFactory.eval( engine, "f => a => f.apply(a)" ) ).call( null, function );
            }

            @Override
            public Bindings parseJson( final String text )
            {
                return (Bindings) ( (JSObject) jsonProto.getMember( "parse" ) ).call( null, text );
            }

            @Override
            public Object eval( final String script )
            {
                return JavascriptHelperFactory.eval( engine, script );
            }

            @Override
            public JsObjectConverter objectConverter()
            {
                return new JsObjectConverter( this );
            }
        };
    }
}
