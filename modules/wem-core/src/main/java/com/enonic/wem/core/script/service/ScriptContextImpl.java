package com.enonic.wem.core.script.service;

import java.util.Stack;

import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeyResolver;
import com.enonic.wem.core.script.ScriptContext;

final class ScriptContextImpl
    implements ScriptContext
{
    private final Stack<ResourceKey> resourceKeys;

    protected ModuleKeyResolver moduleKeyResolver;

    protected ResourceKeyResolver resourceKeyResolver;

    public ScriptContextImpl()
    {
        this.resourceKeys = new Stack<>();
    }

    public void enter( final ResourceKey resourceKey )
    {
        this.resourceKeys.push( resourceKey );
    }

    public void exit()
    {
        this.resourceKeys.pop();
    }

    @Override
    public ResourceKey getResourceKey()
    {
        return this.resourceKeys.peek();
    }

    @Override
    public ModuleKeyResolver getModuleKeyResolver()
    {
        return this.moduleKeyResolver;
    }

    @Override
    public ResourceKeyResolver getResourceKeyResolver()
    {
        return this.resourceKeyResolver;
    }
}
