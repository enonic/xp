package com.enonic.wem.api.command.module;


public final class ModuleCommands
{
    public CreateModule create()
    {
        return new CreateModule();
    }

    public UpdateModule update()
    {
        return new UpdateModule();
    }

    public GetModuleResource getResource()
    {
        return new GetModuleResource();
    }

    public CreateModuleResource createResource()
    {
        return new CreateModuleResource();
    }
}
