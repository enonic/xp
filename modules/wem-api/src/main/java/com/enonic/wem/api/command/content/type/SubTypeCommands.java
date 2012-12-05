package com.enonic.wem.api.command.content.type;

public final class SubTypeCommands
{
    public GetSubTypes get()
    {
        return new GetSubTypes();
    }

    public CreateSubType create()
    {
        return new CreateSubType();
    }

    public UpdateSubTypes update()
    {
        return new UpdateSubTypes();
    }

    public DeleteSubTypes delete()
    {
        return new DeleteSubTypes();
    }

}
