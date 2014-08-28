package com.enonic.wem.script.internal;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptStackElement;
import org.mozilla.javascript.ScriptableObject;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.script.ScriptRunner;

final class RhinoScriptRunner
    implements ScriptRunner
{
    protected RhinoScriptCompiler compiler;

    protected ScriptEnvironment environment;

    private final Map<String, Object> objects;

    private ResourceKey source;

    public RhinoScriptRunner()
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
    public ScriptRunner variable( final String name, final Object value )
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

        new ResolveFunction().install( scope );
        new VariableFunction( this.environment ).install( scope );

        final RequireFunction require = new RequireFunction( scope, this.compiler, this.environment );
        require.install( scope );

        require.requireMain( context, this.source );
    }

    private ResourceProblemException createError( final RhinoException cause )
    {
        final String name = cause.sourceName();
        final ResourceKey source = ResourceKey.from( name );

        final ResourceProblemException.Builder builder = ResourceProblemException.newBuilder();
        builder.cause( cause );
        builder.lineNumber( cause.lineNumber() );
        builder.resource( source );
        builder.message( cause.details() );

        for ( final ScriptStackElement elem : cause.getScriptStack() )
        {
            builder.callLine( elem.fileName, elem.lineNumber );
        }

        return builder.build();
    }
}
