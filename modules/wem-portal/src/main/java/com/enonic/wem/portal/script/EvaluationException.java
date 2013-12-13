package com.enonic.wem.portal.script;

import java.text.MessageFormat;
import java.util.List;

import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptStackElement;

import com.google.common.collect.Lists;

import com.enonic.wem.portal.script.loader.ScriptSource;

public final class EvaluationException
    extends RuntimeException
{
    private final ScriptSource source;

    public EvaluationException( final ScriptSource source, final RhinoException cause )
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

    public List<String> getScriptStack()
    {
        final List<String> list = Lists.newArrayList();
        for ( final ScriptStackElement e : getRhinoException().getScriptStack() )
        {
            list.add( MessageFormat.format( "{0} at line {1}", e.fileName, e.lineNumber ) );
        }

        return list;
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
