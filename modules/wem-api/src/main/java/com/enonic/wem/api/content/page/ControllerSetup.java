package com.enonic.wem.api.content.page;


import com.enonic.wem.api.module.ModuleResourceKey;

public class ControllerSetup
{
    ModuleResourceKey source;

    ControllerParams params;

    public ModuleResourceKey getSource()
    {
        return source;
    }

    public ControllerParams getParams()
    {
        return params;
    }
}
