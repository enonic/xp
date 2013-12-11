package com.enonic.wem.portal.script;

import org.mozilla.javascript.RhinoException;

import com.enonic.wem.portal.script.loader.ScriptSource;

public final class EvalScriptException
    extends RuntimeException
{
    private final ScriptSource source;

    public EvalScriptException( final ScriptSource source, final RhinoException cause )
    {
        super( cause );
        this.source = source;
    }

    public ScriptSource getSource()
    {
        return this.source;
    }

    public int getLineNumber()
    {
        return getRhinoException().lineNumber();
    }

    public int getColumnNumber()
    {
        return getRhinoException().columnNumber();
    }

    private RhinoException getRhinoException()
    {
        return (RhinoException) getCause();
    }

    @Override
    public String getMessage()
    {
        return getRhinoException().details();
    }
}
