package com.enonic.wem.api.command;

import com.enonic.wem.api.command.resource.GetResourceBuilder;

public abstract class Commands
{
    public static GetResourceBuilder getResource()
    {
        return new GetResourceBuilder();
    }
}
