package com.enonic.wem.core.script;

public interface ScriptRunner
{
    public ScriptRunner variable( String name, Object value );

    public void execute();
}
