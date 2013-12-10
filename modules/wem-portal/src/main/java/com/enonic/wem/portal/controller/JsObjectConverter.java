package com.enonic.wem.portal.controller;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.Scriptable;

final class JsObjectConverter
{
    public Object convert( final Object from )
    {
        if ( from instanceof Scriptable )
        {
            return convert( (Scriptable) from );
        }

        return from.toString();
    }

    private String convert( final Scriptable from )
    {
        final Context context = Context.enter();

        try
        {
            return NativeJSON.stringify( context, from, from, null, null ).toString();
        }
        finally
        {
            Context.exit();
        }
    }
}
