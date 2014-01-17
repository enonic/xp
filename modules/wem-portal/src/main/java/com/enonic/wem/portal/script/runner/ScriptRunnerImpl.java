package com.enonic.wem.portal.script.runner;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptStackElement;
import org.mozilla.javascript.Scriptable;

import com.google.common.collect.Maps;

import com.enonic.wem.portal.script.SourceException;
import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.lib.ContextScriptBean;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.loader.ScriptSource;

public final class ScriptRunnerImpl
    implements ScriptRunner
{
    private Scriptable scope;

    protected ScriptCompiler compiler;

    protected ScriptLoader scriptLoader;

    private final Map<String, Object> objects;

    private ScriptSource source;

    protected ContextScriptBean contextServiceBean;

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

        this.contextServiceBean.setModule( this.source.getModule() );
        this.contextServiceBean.install( context );

        try
        {
            initializeScope();
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
            ContextScriptBean.remove( context );
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

    private void initializeScope()
    {
        final Context context = Context.getCurrentContext();
        this.scope = context.initStandardObjects();
    }

    private SourceException createError( final RhinoException cause )
    {
        final String name = cause.sourceName();
        final ScriptSource source = this.scriptLoader.load( name );

        final SourceException.Builder builder = SourceException.newBuilder();
        builder.cause( cause );
        builder.lineNumber( cause.lineNumber() );
        builder.resource( source.getResource() );
        builder.path( source.getPath() );
        builder.message( cause.details() );

        for ( final ScriptStackElement elem : cause.getScriptStack() )
        {
            builder.callLine( elem.fileName, elem.lineNumber );
        }

        return builder.build();
    }

    public void setScriptLoader( final ScriptLoader scriptLoader )
    {
        this.scriptLoader = scriptLoader;
    }

    public void setCompiler( final ScriptCompiler compiler )
    {
        this.compiler = compiler;
    }

    public void setContextServiceBean( final ContextScriptBean contextServiceBean )
    {
        this.contextServiceBean = contextServiceBean;
    }
}
