package com.enonic.wem.api.command.entity;


public class NodeCommands
{
    public CreateNode create()
    {
        return new CreateNode();
    }

    public UpdateNode update()
    {
        return new UpdateNode();
    }

    public NodeGetCommands get()
    {
        return new NodeGetCommands();
    }
}
