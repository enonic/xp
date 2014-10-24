package com.enonic.wem.script.internal;

import javax.script.Bindings;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.command.CommandInvoker;

public interface ScriptExecutor
{
    public Bindings createBindings();

    public CommandInvoker getInvoker();

    public void execute( Bindings bindings, ResourceKey script );

    public Object invokeMethod( Object scope, String name, Object... args );
}
