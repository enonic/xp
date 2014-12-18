package com.enonic.wem.script.internal.nashorn;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ScriptObject;

public final class NashornHelper
{
    private final static NashornHelper INSTANCE = new NashornHelper();

    private final ScriptEngine engine;

    private NashornHelper()
    {
        this.engine = new NashornScriptEngineFactory().getScriptEngine();
    }

    public static ScriptEngine getScriptEngine()
    {
        return INSTANCE.engine;
    }

    public static ScriptObject newScriptObject()
    {
        return Global.newEmptyInstance();
    }

    public static ScriptObject newScriptArray( final Object... array )
    {
        return Global.allocate( array );
    }

    public static ScriptObject getCurrentGlobal()
    {
        return Context.getGlobal();
    }

    public static ScriptObject findGlobalObject( final Bindings bindings )
    {
        final Bindings scope = bindings != null ? bindings : INSTANCE.engine.createBindings();
        return ( (ScriptObjectMirror) scope ).to( Global.class );
    }

    public static void setCurrentGlobal( final ScriptObject global )
    {
        Context.setGlobal( global );
    }
}
