package com.enonic.wem.portal.script.exception;

public abstract class ScriptException
    extends RuntimeException
{
    public ScriptException( final Throwable cause )
    {
        super( cause );
    }

    public ScriptException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
