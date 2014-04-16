package com.enonic.wem.core.script.service;

import java.text.MessageFormat;
import java.util.Map;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeyResolver;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.core.script.compiler.ScriptCompiler;

final class RequireFunction
    extends BaseFunction
{
    private final Map<ResourceKey, Scriptable> exportMap;

    protected ResourceService resourceService;

    protected ScriptCompiler scriptCompiler;

    protected ScriptContextImpl scriptContext;

    public RequireFunction()
    {
        this.exportMap = Maps.newHashMap();
    }

    @Override
    public String getFunctionName()
    {
        return "require";
    }

    @Override
    public int getArity()
    {
        return 1;
    }

    @Override
    public int getLength()
    {
        return 1;
    }

    @Override
    public Scriptable construct( final Context cx, final Scriptable scope, final Object[] args )
    {
        throw error( cx, scope, "require() can not be invoked as a constructor" );
    }

    @Override
    public Object call( final Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args )
    {
        if ( ( args == null ) || ( args.length < 1 ) )
        {
            throw error( cx, scope, "require() needs one argument" );
        }

        final String name = (String) Context.jsToJava( args[0], String.class );
        final Resource source = resolveSource( cx, scope, name );

        final Scriptable exports = this.exportMap.get( source.getKey() );
        if ( exports != null )
        {
            return exports;
        }

        return doCall( cx, scope, source );
    }

    private Resource resolveSource( final Context cx, final Scriptable scope, final String name )
    {
        final String jsName = name.endsWith( ".js" ) ? name : ( name + ".js" );

        try
        {
            final ResourceKeyResolver resolver = this.scriptContext.getResourceKeyResolver();
            final ResourceKey resourceKey = resolver.resolve( this.scriptContext.getResourceKey(), jsName );
            return this.resourceService.getResource( resourceKey );
        }
        catch ( final Exception e )
        {
            throw ScriptRuntime.throwError( cx, scope, e.getMessage() );
        }
    }

    private Object doCall( final Context cx, final Scriptable scope, final Resource source )
    {
        final Script script = this.scriptCompiler.compile( cx, source );

        final Scriptable exports = cx.newObject( scope );
        final Scriptable moduleObject = cx.newObject( scope );

        final TopLevel newScope = new TopLevel();
        newScope.setPrototype( scope );
        newScope.put( "exports", newScope, exports );
        newScope.put( "module", newScope, moduleObject );
        moduleObject.put( "exports", moduleObject, exports );

        this.scriptContext.enter( source.getKey() );

        try
        {
            script.exec( cx, newScope );
        }
        finally
        {
            this.scriptContext.exit();
        }

        final Scriptable newExports = ScriptRuntime.toObject( scope, exports );
        this.exportMap.put( source.getKey(), newExports );
        return newExports;
    }

    public void install( final Scriptable scope )
    {
        ScriptableObject.putProperty( scope, "require", this );
    }

    private JavaScriptException error( final Context context, final Scriptable scope, final String message, final Object... args )
    {
        return ScriptRuntime.throwError( context, scope, MessageFormat.format( message, args ) );
    }
}
