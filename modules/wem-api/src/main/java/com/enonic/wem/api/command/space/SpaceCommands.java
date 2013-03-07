package com.enonic.wem.api.command.space;


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

    public UpdateSpace update()
    {
        return new UpdateSpace();
    }

    public DeleteSpace delete()
    {
        return new DeleteSpace();
    }

    public RenameSpace rename()
    {
        return new RenameSpace();
    }
}
