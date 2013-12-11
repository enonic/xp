package com.enonic.wem.portal.script.runner;

import com.enonic.wem.portal.script.loader.ScriptSource;

public interface ScriptRunner
{
    public ScriptRunner source( ScriptSource source );

    public ScriptRunner property( String name, Object value );

    public void execute();
}
