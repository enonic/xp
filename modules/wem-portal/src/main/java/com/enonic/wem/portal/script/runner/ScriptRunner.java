package com.enonic.wem.portal.script.runner;

import com.enonic.wem.api.resource.Resource;

public interface ScriptRunner
{
    public ScriptRunner source( Resource source );

    public ScriptRunner property( String name, Object value );

    public void execute();
}
