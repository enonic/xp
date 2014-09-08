package com.enonic.wem.script.internal;

import com.enonic.wem.script.ScriptLibrary;

public interface ScriptEnvironment
{
    public ScriptLibrary getLibrary( String name );
}
