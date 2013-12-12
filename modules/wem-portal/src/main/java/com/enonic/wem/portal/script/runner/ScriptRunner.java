package com.enonic.wem.portal.script.runner;

import com.enonic.wem.core.module.ModuleKeyResolver;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.loader.ScriptSource;

public interface ScriptRunner
{
    public ScriptLoader getLoader();

    public ScriptRunner source( ScriptSource source );

    public ScriptRunner property( String name, Object value );

    public ScriptRunner moduleKeyResolver( ModuleKeyResolver value );

    public void execute();
}
