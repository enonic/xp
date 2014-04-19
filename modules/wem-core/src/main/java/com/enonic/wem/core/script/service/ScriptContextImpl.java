package com.enonic.wem.core.script.service;

import java.util.Stack;

import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleResourceKeyResolver;
import com.enonic.wem.core.script.ScriptContext;

final class ScriptContextImpl
    implements ScriptContext
{
    private final Stack<ModuleResourceKey> resourceKeys;

    protected ModuleKeyResolver moduleKeyResolver;

    protected ModuleResourceKeyResolver resourceKeyResolver;

    public ScriptContextImpl()
    {
        this.resourceKeys = new Stack<>();
    }

    public void enter( final ModuleResourceKey resourceKey )
    {
        this.resourceKeys.push( resourceKey );
    }

    public void exit()
    {
        this.resourceKeys.pop();
    }

    @Override
    public ModuleResourceKey getResourceKey()
    {
        return this.resourceKeys.peek();
    }

    @Override
    public ModuleKeyResolver getModuleKeyResolver()
    {
        return this.moduleKeyResolver;
    }

    @Override
    public ModuleResourceKeyResolver getResourceKeyResolver()
    {
        return this.resourceKeyResolver;
    }
}
