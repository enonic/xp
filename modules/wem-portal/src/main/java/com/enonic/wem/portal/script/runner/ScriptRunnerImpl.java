package com.enonic.wem.portal.script.runner;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptStackElement;
import org.mozilla.javascript.Scriptable;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlResolver;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.script.SourceException;
import com.enonic.wem.portal.script.lib.ContextScriptBean;

final class ScriptRunnerImpl
    implements ScriptRunner
{
    private Scriptable scope;

    protected ScriptCompiler compiler;

    private final Map<String, Object> objects;

    private Resource source;

    protected ContextScriptBean contextServiceBean;

    public ScriptRunnerImpl()
    {
        this.objects = Maps.newHashMap();
    }

    @Override
    public ScriptRunner source( final Resource source )
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

        this.contextServiceBean.setModule( this.source.getKey().getModule() );
        this.contextServiceBean.install();
        final JsContext portalContext = (JsContext) objects.get( "portal" );
        this.contextServiceBean.setJsContext( portalContext );

        try
        {
            initializeScope();
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
            ContextScriptBean.remove();
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

    private void initializeScope()
    {
        final Context context = Context.getCurrentContext();
        this.scope = context.initStandardObjects();
    }

    private SourceException createError( final RhinoException cause )
    {
        final String name = cause.sourceName();
        final ResourceKey source = ResourceKey.from( name );

        final SourceException.Builder builder = SourceException.newBuilder();
        builder.cause( cause );
        builder.lineNumber( cause.lineNumber() );
        builder.resource( source );
        builder.path( ResourceUrlResolver.resolve( source ) );
        builder.message( cause.details() );

        for ( final ScriptStackElement elem : cause.getScriptStack() )
        {
            builder.callLine( elem.fileName, elem.lineNumber );
        }

        return builder.build();
    }
}
