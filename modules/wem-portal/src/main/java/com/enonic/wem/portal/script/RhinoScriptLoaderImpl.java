package com.enonic.wem.portal.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;

public final class RhinoScriptLoaderImpl
    implements RhinoScriptLoader
{
    private final ScriptableObject globalScope;

    public RhinoScriptLoaderImpl()
    {
        this.globalScope = createGlobalScope();
    }

    private Require installRequire( final Context context, final ScriptLocations locations )
    {
        final RequireBuilder builder = new RequireBuilder();
        builder.setSandboxed( false );

        // final ModuleScriptProviderImpl moduleProvider = new ModuleScriptProviderImpl( this.scriptDir );
        // builder.setModuleScriptProvider( moduleProvider );

        return builder.createRequire( context, this.globalScope );
    }

    private static ScriptableObject createGlobalScope()
    {
        final Context context = Context.enter();
        context.setOptimizationLevel( 2 );

        try
        {
            return context.initStandardObjects();
        }
        finally
        {
            Context.exit();
        }
    }

    @Override
    public NativeObject load( final String name, final ScriptLocations locations )
        throws Exception
    {
        final Context context = Context.enter();
        context.setOptimizationLevel( 2 );

        try
        {
            final Require require = installRequire( context, locations );
            final Scriptable script = require.requireMain( context, name );
            return (NativeObject) script;
        }
        finally
        {
            Context.exit();
        }
    }
}
