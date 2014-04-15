package com.enonic.wem.core.script.service;

import java.util.Map;

import org.mozilla.javascript.Context;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.core.script.ScriptRunner;
import com.enonic.wem.core.script.compiler.ScriptCompiler;

final class ScriptRunnerImpl
    implements ScriptRunner
{
    protected ResourceService resourceService;

    protected ScriptCompiler compiler;

    private final Map<String, Object> binding;

    private ResourceKey resourceKey;

    private Resource resource;

    public ScriptRunnerImpl()
    {
        this.binding = Maps.newHashMap();
    }

    @Override
    public ScriptRunner source( final ResourceKey source )
    {
        this.resourceKey = source;
        return this;
    }

    @Override
    public ScriptRunner binding( final String name, final Object value )
    {
        this.binding.put( name, value );
        return this;
    }

    @Override
    public void execute()
    {
        this.resource = this.resourceService.getResource( this.resourceKey );

        final Context context = Context.enter();

    }
}
