package com.enonic.wem.api.command.module;

public final class ModuleCommands
{
    public GetModuleResource getResource()
    {
        return new GetModuleResource();
    }

    public CreateModuleResource createResource()
    {
        return new CreateModuleResource();
    }
}
