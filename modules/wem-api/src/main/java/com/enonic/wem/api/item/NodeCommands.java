package com.enonic.wem.api.item;


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
