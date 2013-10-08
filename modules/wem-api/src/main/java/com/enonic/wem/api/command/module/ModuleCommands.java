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

    public DeleteModule delete()
    {
        return new DeleteModule();
    }

    public GetModules get()
    {
        return new GetModules();
    }

    public GetModules getAll()
    {
        return new GetModules().all();
    }

    public GetModuleResource getResource()
    {
        return new GetModuleResource();
    }
}
