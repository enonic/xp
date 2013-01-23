package com.enonic.wem.api.command.content.space;


public final class SpaceCommands
{
    public CreateSpace create()
    {
        return new CreateSpace();
    }

    public GetSpaces get()
    {
        return new GetSpaces();
    }
}
