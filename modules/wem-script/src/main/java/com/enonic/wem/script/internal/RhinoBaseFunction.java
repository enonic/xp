package com.enonic.wem.script.internal;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

abstract class RhinoBaseFunction
    extends BaseFunction
{
    private final String name;

    public RhinoBaseFunction( final String name )
    {
        this.name = name;
    }

    protected final void checkArguments( final Context context, final Scriptable scope, final Object[] args )
    {
        if ( ( args == null ) || ( args.length < getArity() ) )
        {
            throw ScriptRuntime.throwError( context, scope, this.name + "() has illegal number of arguments" );
        }
    }

    public final void install( final Scriptable scope )
    {
        ScriptableObject.putProperty( scope, this.name, this );
    }
}
