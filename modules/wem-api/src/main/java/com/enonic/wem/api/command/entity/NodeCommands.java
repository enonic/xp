package com.enonic.wem.api.command.entity;


import com.enonic.wem.api.entity.UpdateNode;

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
}
