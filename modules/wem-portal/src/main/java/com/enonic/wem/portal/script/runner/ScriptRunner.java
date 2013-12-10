package com.enonic.wem.portal.script.runner;

import java.nio.file.Path;

public interface ScriptRunner
{
    public ScriptRunner file( Path file );

    public ScriptRunner object( String name, Object value );

    public void execute();
}
