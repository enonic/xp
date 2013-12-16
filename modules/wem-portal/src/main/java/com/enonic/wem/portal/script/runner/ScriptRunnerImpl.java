package com.enonic.wem.portal.script.runner;

import java.util.Map;

import javax.inject.Provider;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.wem.portal.script.EvaluationException;
import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.loader.ScriptSource;
import com.enonic.wem.portal.script.runtime.JsApiBridge;
import com.enonic.wem.portal.script.runtime.RootRuntimeObject;

final class ScriptRunnerImpl
    implements ScriptRunner
{
    private Scriptable scope;

    protected ScriptCompiler compiler;

    protected ScriptLoader scriptLoader;

    private final Map<String, Object> objects;

    private ScriptSource source;

    protected JsApiBridge apiBridge;

    protected Provider<RootRuntimeObject> rootRuntimeObjects;

    public ScriptRunnerImpl()
    {
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
    public void execute()
    {
        final Context context = Context.enter();

        try
        {
            initializeScope( context );
            setupProperties();
            installRequire();
            setObjectsToScope();

            final Script script = this.compiler.compile( context, this.source );
            script.exec( context, this.scope );
        }
        catch ( final RhinoException e )
        {
            throw createError( e );
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
        function.setSource( this.source );
        function.install( this.scope );
    }

    private void initializeScope( final Context context )
    {
        this.scope = context.initStandardObjects();
        final RootRuntimeObject runtime = this.rootRuntimeObjects.get();
        runtime.setModule( this.source.getModule() );
        runtime.setScope( this.scope );

        property( "__wem", runtime );
    }

    private void setupProperties()
    {
        property( "__log", LoggerFactory.getLogger( getClass() ) );
        property( "__api", this.apiBridge );
        this.apiBridge.setScope( this.scope );
    }

    private EvaluationException createError( final RhinoException cause )
    {
        final String name = cause.sourceName();
        final ScriptSource source = this.scriptLoader.load( name );
        return new EvaluationException( source, cause );
    }
}
