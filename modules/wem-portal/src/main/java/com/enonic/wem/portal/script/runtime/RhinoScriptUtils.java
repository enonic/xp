package com.enonic.wem.portal.script.runtime;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.slf4j.Logger;

public class RhinoScriptUtils
{
    private final Scriptable scope;

    public RhinoScriptUtils( final Scriptable scope )
    {
        this.scope = scope;
    }

    public Logger getLogger()
    {
        return null;
    }

    public void traceHelper( final Function function, final Object... args )
    {
        final Context cx = Context.getCurrentContext();
        final Scriptable scope = ScriptableObject.getTopLevelScope( function );
        final EcmaError error = ScriptRuntime.constructError( "Trace", "" );
        final WrapFactory wrapFactory = cx.getWrapFactory();

        final Scriptable thisObj = wrapFactory.wrapAsJavaObject( cx, scope, error, null );
        for ( int i = 0; i < args.length; i++ )
        {
            args[i] = wrapFactory.wrap( cx, scope, args[i], null );
        }

        function.call( cx, scope, thisObj, args );
    }

    public NativeJavaClass javaClass( final String name )
        throws Exception
    {
        return new NativeJavaClass( this.scope, Class.forName( name ) );
    }
}
