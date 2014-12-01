package com.enonic.wem.script.internal.json;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.JSObject;

public final class JsonFunctions
{
    public JSObject parse( final String value )
        throws Exception
    {
        return null;
    }

    public String stringify( final Object value )
        throws Exception
    {
        return null;
    }

    public void register( final Bindings bindings )
    {
        bindings.put( "JSON", this );
    }
}
