package com.enonic.wem.portal.script.runner;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import com.google.common.collect.Maps;

import com.enonic.wem.core.module.ModuleKeyResolver;
import com.enonic.wem.portal.script.EvaluationException;
import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.loader.ScriptSource;
import com.enonic.wem.portal.script.require.RequireFunction;

final class ScriptRunnerImpl
    implements ScriptRunner
{
    private final Scriptable scope;

    private final ScriptCompiler compiler;

    private final ScriptLoader scriptLoader;

    private final Map<String, Object> objects;

    private ScriptSource source;

    private ModuleKeyResolver moduleKeyResolver;

    public ScriptRunnerImpl( final ScriptCompiler compiler, final ScriptLoader scriptLoader, final Scriptable scope )
    {
        this.compiler = compiler;
        this.scriptLoader = scriptLoader;
        this.scope = scope;
        this.objects = Maps.newHashMap();
    }

    @Override
    public ScriptLoader getLoader()
    {
        return this.scriptLoader;
    }

    @Override
    public ScriptRunner source( final ScriptSource source )
    {
        this.source = source;
        return this;
    }

    @Override
    public ScriptRunner property( final String name, final Object value )
    {
        this.objects.put( name, value );
        return this;
    }

    @Override
    public ScriptRunner moduleKeyResolver( final ModuleKeyResolver value )
    {
        this.moduleKeyResolver = value;
        return this;
    }

    @Override
    public void execute()
    {
        final Context context = Context.enter();

        try
        {
            installRequire();
            setObjectsToScope();

            final Script script = this.compiler.compile( context, this.source );
            script.exec( context, this.scope );
        }
        catch ( final RhinoException e )
        {
            throw new EvaluationException( this.source, e );
        }
        finally
        {
            Context.exit();
        }
    }

    private void setObjectsToScope()
    {
        for ( final Map.Entry<String, Object> entry : this.objects.entrySet() )
        {
            this.scope.put( entry.getKey(), this.scope, Context.javaToJS( entry.getValue(), this.scope ) );
        }
    }

    private void installRequire()
    {
        final RequireFunction function = new RequireFunction();
        function.setScriptCompiler( this.compiler );
        function.setScriptLoader( this.scriptLoader );
        function.setModuleKeyResolver( this.moduleKeyResolver );
        function.setSource( this.source );
        function.install( this.scope );
    }
}
