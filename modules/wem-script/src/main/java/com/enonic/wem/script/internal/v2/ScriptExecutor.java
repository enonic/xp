package com.enonic.wem.script.internal.v2;

import javax.script.Bindings;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptExecutor
{
    public Bindings createBindings();

    public void execute( Bindings bindings, ResourceKey script );

    public Object invokeMethod( Object scope, String name, Object... args );
}
