package com.enonic.wem.portal.script.helper;

import java.text.MessageFormat;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

public final class ScriptHelper
{
    public static JavaScriptException error( final String message, final Object... args )
    {
        final Context context = Context.getCurrentContext();
        final Scriptable scope = topLevelScope();
        return ScriptRuntime.throwError( context, scope, MessageFormat.format( message, args ) );
    }

    public static Scriptable topLevelScope()
    {
        final Context context = Context.getCurrentContext();
        return ScriptRuntime.getTopCallScope( context );
    }
}
