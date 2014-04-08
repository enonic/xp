package com.enonic.wem.core.script.service;

import com.enonic.wem.core.module.source.ModuleSource;

final class RunnerContextImpl
    implements RunnerContext
{
    @Override
    public ModuleSource getSource()
    {
        return null;
    }

    @Override
    public Object require( final String name )
    {
        return null;
    }

    @Override
    public Object getService( final String name )
    {
        return null;
    }

    public Object executeMain()
    {
        return null;
    }
}
