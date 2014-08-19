package com.enonic.wem.script.internal;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.ResourceKey;

final class RequireFunction
    extends RhinoBaseFunction
{
    private final static String EXPORTS_NAME = "exports";

    private final Map<ResourceKey, Scriptable> exportedInterfaces;

    private final Scriptable nativeScope;

    private final RhinoScriptCompiler compiler;

    private final ScriptEnvironment environment;

    public RequireFunction( final Scriptable nativeScope, final RhinoScriptCompiler compiler, final ScriptEnvironment environment )
    {
        super( "require" );
        this.nativeScope = nativeScope;
        this.compiler = compiler;
        this.exportedInterfaces = Maps.newConcurrentMap();
        this.environment = environment;
        setPrototype( ScriptableObject.getFunctionPrototype( nativeScope ) );
    }

    @Override
    public int getArity()
    {
        return 1;
    }

    @Override
    public Object call( final Context context, final Scriptable scope, final Scriptable thisObj, final Object[] args )
    {
        checkArguments( context, scope, args );

        final RequireModuleScope moduleScope = (RequireModuleScope) thisObj;
        final String name = (String) Context.jsToJava( args[0], String.class );

        final ResourceKey resource = moduleScope.resolveScript( name );
        return getExportedInterface( context, resource );
    }

    public Scriptable requireMain( final Context context, final ResourceKey key )
    {
        return getExportedInterface( context, key );
    }

    private Scriptable getExportedInterface( final Context context, final ResourceKey key )
    {
        Scriptable exports = this.exportedInterfaces.get( key );
        if ( exports != null )
        {
            return exports;
        }

        exports = context.newObject( this.nativeScope );

        final Script script = this.compiler.compile( context, key );
        final Scriptable newExports = executeScript( context, exports, key, script );

        if ( exports != newExports )
        {
            exports = newExports;
        }

        return exports;
    }

    private Scriptable executeScript( final Context context, final Scriptable exports, final ResourceKey resource, final Script script )
    {
        final ScriptableObject moduleObject = (ScriptableObject) context.newObject( this.nativeScope );

        final Scriptable executionScope = new RequireModuleScope( this.nativeScope, resource, this.environment );
        executionScope.put( EXPORTS_NAME, executionScope, exports );
        moduleObject.put( EXPORTS_NAME, moduleObject, exports );

        install( executionScope );

        script.exec( context, executionScope );
        return ScriptRuntime.toObject( this.nativeScope, ScriptableObject.getProperty( moduleObject, EXPORTS_NAME ) );
    }
}
