package com.enonic.wem.core.script.service;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptStackElement;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;

import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeyResolver;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.core.script.ScriptException;
import com.enonic.wem.core.script.ScriptRunner;
import com.enonic.wem.core.script.compiler.ScriptCompiler;

final class ScriptRunnerImpl
    implements ScriptRunner
{
    private Scriptable scope;

    protected ResourceService resourceService;

    protected ScriptCompiler compiler;

    private final Map<String, Object> binding;

    private ResourceKey resourceKey;

    private final ScriptContextImpl scriptContext;

    private ModuleKeyResolver moduleKeyResolver;

    public ScriptRunnerImpl()
    {
        this.binding = Maps.newHashMap();
        this.scriptContext = new ScriptContextImpl();
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
    }

    private void doExecute( final Context context )
    {
        initializeScope();
        setObjectsToScope();

        final Require require = installRequire( context );
        require.requireMain( context, this.resourceKey.toString() );

        /*final Script script = this.compiler.compile( context, resource );
        script.exec( context, this.scope );
        */
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

        /*
        final RequireFunction function = new RequireFunction();
        function.scriptCompiler = this.compiler;
        function.scriptContext = this.scriptContext;
        function.resourceService = this.resourceService;
        function.install( this.scope );
        */
    }

    @Override
    public ScriptRunner moduleKeyResolver( final ModuleKeyResolver moduleKeyResolver )
    {
        this.moduleKeyResolver = moduleKeyResolver;
        return this;
    }
}
