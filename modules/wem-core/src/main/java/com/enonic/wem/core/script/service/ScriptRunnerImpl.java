package com.enonic.wem.core.script.service;

import java.util.Map;

import javax.script.ScriptEngine;

import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.core.script.ScriptRunner;

final class ScriptRunnerImpl
    implements ScriptRunner
{
    protected ScriptEngine scriptEngine;

    protected ResourceService resourceService;

    private final Map<String, Object> binding;

    private ModuleResourceKey resourceKey;

    public ScriptRunnerImpl()
    {
        this.binding = Maps.newHashMap();
    }

    @Override
    public ScriptRunner source( final ModuleResourceKey source )
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
        final Resource resource = this.resourceService.getResource( this.resourceKey );

        /*
        this.scriptEngine.eval()

        final Context context = Context.enter();

        this.scriptContext.moduleKeyResolver = this.moduleKeyResolver;
        this.scriptContext.resourceKeyResolver = new ResourceKeyResolver( this.moduleKeyResolver );
        this.scriptContext.enter( this.resourceKey );

        try
        {
            doExecute( context );
        }
        catch ( final RhinoException e )
        {
            throw createError( e );
        }
        finally
        {
            Context.exit();
            this.scriptContext.exit();
        }
        */
    }

    /*
    private void doExecute( final Context context )
    {
        initializeScope();
        setObjectsToScope();

        final Require require = installRequire( context );
        require.requireMain( context, this.resourceKey.toString() );
    }


    private void setObjectsToScope()
    {
        for ( final Map.Entry<String, Object> entry : this.binding.entrySet() )
        {
            this.scope.put( entry.getKey(), this.scope, Context.javaToJS( entry.getValue(), this.scope ) );
        }
    }

    private void initializeScope()
    {
        final Context context = Context.getCurrentContext();
        this.scope = context.initStandardObjects();
    }

    private ScriptException createError( final RhinoException cause )
    {
        final String name = cause.sourceName();

        final ScriptException.Builder builder = ScriptException.newBuilder();
        builder.cause( cause );
        builder.lineNumber( cause.lineNumber() );
        builder.resource( ResourceKey.from( name ) );
        builder.message( cause.details() );

        for ( final ScriptStackElement elem : cause.getScriptStack() )
        {
            builder.callLine( elem.fileName, elem.lineNumber );
        }

        return builder.build();
    }

    private Require installRequire( final Context context )
    {
        final ScriptProviderImpl provider = new ScriptProviderImpl();
        provider.scriptContext = this.scriptContext;
        provider.resourceService = this.resourceService;
        provider.scriptCompiler = this.compiler;

        final RequireBuilder builder = new RequireBuilder();
        builder.setModuleScriptProvider( provider );
        builder.setSandboxed( false );

        final Require require = builder.createRequire( context, this.scope );
        require.install( this.scope );
        return require;
    }
    */
}
