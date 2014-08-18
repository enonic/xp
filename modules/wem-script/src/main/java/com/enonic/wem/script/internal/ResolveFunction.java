package com.enonic.wem.script.internal;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

final class ResolveFunction
    extends BaseFunction
{
    private final static String NAME = "resolve";

    @Override
    public int getArity()
    {
        return 1;
    }

    @Override
    public Object call( final Context context, final Scriptable scope, final Scriptable thisObj, final Object[] args )
    {
        if ( ( args == null ) || ( args.length < 1 ) )
        {
            throw ScriptRuntime.throwError( context, scope, NAME + "() needs one argument" );
        }

        final RequireModuleScope moduleScope = (RequireModuleScope) thisObj;
        final String name = (String) Context.jsToJava( args[0], String.class );

        return moduleScope.resolveResource( name );
    }

    public ResolveFunction install( final Scriptable scope )
    {
        ScriptableObject.putProperty( scope, NAME, this );
        return this;
    }
}
