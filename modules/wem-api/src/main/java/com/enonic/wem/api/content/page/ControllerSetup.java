package com.enonic.wem.api.content.page;


import com.enonic.wem.api.module.ModuleResourceKey;

public final class ControllerSetup
{
    private final ModuleResourceKey source;

    private final ControllerParams params;

    public ControllerSetup( final ModuleResourceKey source, final ControllerParams params )
    {
        this.source = source;
        this.params = params;
    }

    public ControllerSetup( final ModuleResourceKey source )
    {
        this( source, ControllerParams.empty() );
    }

    public ModuleResourceKey getSource()
    {
        return source;
    }

    public ControllerParams getParams()
    {
        return params;
    }
}
