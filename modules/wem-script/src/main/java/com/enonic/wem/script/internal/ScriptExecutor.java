package com.enonic.wem.script.internal;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptObject;

public interface ScriptExecutor
{
    public Bindings executeRequire( ResourceKey script );

    public void addGlobalBinding( String key, Object value );

    public ScriptObject invokeMethod( Object scope, JSObject func, Object... args );

    public Object convertToJsObject( Object value );
}
