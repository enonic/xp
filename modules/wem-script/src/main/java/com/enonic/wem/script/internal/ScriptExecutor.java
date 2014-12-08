package com.enonic.wem.script.internal;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptExecutor
{
    public Bindings executeRequire( ResourceKey script );

    public Object invokeMethod( Object scope, String name, Object... args );

    public Object invokeMethod( Object scope, JSObject func, Object... args );
}
