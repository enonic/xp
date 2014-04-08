package com.enonic.wem.core.script.service;

import com.enonic.wem.core.module.source.ModuleSource;

public interface RunnerContext
{
    public ModuleSource getSource();

    public Object require( String name );

    public Object getService( String name );
}
