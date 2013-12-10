package com.enonic.wem.portal.script;

public final class ScriptException
    extends RuntimeException
{
    public ScriptException( final String message )
    {
        super( message );
    }

    public ScriptException( final Throwable cause )
    {
        super( cause );
    }

    public ScriptException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
