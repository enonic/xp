package com.enonic.wem.script.internal.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

final class ResolveFunction
    extends RhinoBaseFunction
{
    public ResolveFunction()
    {
        super( "resolve" );
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

        return moduleScope.resolveResource( name );
    }
}
