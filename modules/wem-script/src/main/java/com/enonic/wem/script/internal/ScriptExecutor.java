package com.enonic.wem.script.internal;

import javax.script.Bindings;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptLibrary;

public interface ScriptExecutor
{
    public Bindings createBindings();

    public ScriptLibrary getLibrary( String name );

    public void execute( Bindings bindings, ResourceKey script );

    public Object invokeMethod( Object scope, String name, Object... args );
}
