package com.enonic.wem.script.internal;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

final class VariableFunction
    extends RhinoBaseFunction
{
    private final ScriptEnvironment environment;

    public VariableFunction( final ScriptEnvironment environment )
    {
        super( "__" );
        this.environment = environment;
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

        final String name = (String) Context.jsToJava( args[0], String.class );
        return this.environment.getVariable( name );
    }
}
