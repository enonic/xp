package com.enonic.wem.script.internal;

import javax.script.Bindings;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptExecutor
{
    public Bindings createBindings();

    public ScriptEnvironment getEnvironment();

    public void execute( Bindings bindings, ResourceKey script );

    public Object invokeMethod( Object scope, String name, Object... args );
}
