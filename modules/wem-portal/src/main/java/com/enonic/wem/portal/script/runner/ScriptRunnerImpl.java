package com.enonic.wem.portal.script.runner;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptStackElement;
import org.mozilla.javascript.ScriptableObject;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlResolver;
import com.enonic.wem.portal.script.SourceException;

final class ScriptRunnerImpl
    implements ScriptRunner
{
    protected ScriptCompiler compiler;

    private final Map<String, Object> objects;

    private ResourceKey source;

    public ScriptRunnerImpl()
    {
        this.objects = Maps.newHashMap();
    }

    @Override
    public ScriptRunner source( final ResourceKey source )
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
            doExecute( context );
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

    private void doExecute( final Context context )
    {
        final ScriptableObject scope = context.initStandardObjects();

        for ( final Map.Entry<String, Object> entry : this.objects.entrySet() )
        {
            scope.put( entry.getKey(), scope, Context.javaToJS( entry.getValue(), scope ) );
        }

        final RequireFunction require = new RequireFunction( scope, this.compiler );
        require.install( scope );

        require.requireMain( context, this.source );
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
