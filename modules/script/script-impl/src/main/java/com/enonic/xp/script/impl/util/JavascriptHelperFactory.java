package com.enonic.xp.script.impl.util;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public final class JavascriptHelperFactory
{
    private final ScriptEngine engine;

    public JavascriptHelperFactory( final ScriptEngine engine )
    {
        this.engine = engine;
    }

    public JavascriptHelper create()
    {
        final Bindings bindings = this.engine.getBindings( ScriptContext.ENGINE_SCOPE );
        final ScriptObjectMirror arrayProto = (ScriptObjectMirror) bindings.get( "Array" );
        final ScriptObjectMirror objectProto = (ScriptObjectMirror) bindings.get( "Object" );

        return new JavascriptHelper()
        {
            @Override
            public ScriptObjectMirror newJsArray()
            {
                return (ScriptObjectMirror) arrayProto.newObject();
            }

            @Override
            public ScriptObjectMirror newJsObject()
            {
                return (ScriptObjectMirror) objectProto.newObject();
            }
        };
    }
}
