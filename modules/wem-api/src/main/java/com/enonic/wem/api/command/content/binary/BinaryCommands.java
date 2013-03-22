package com.enonic.wem.api.command.content.binary;

public final class BinaryCommands
{
    public CreateBinary create()
    {
        return new CreateBinary();
    }

    public DeleteBinary delete()
    {
        return new DeleteBinary();
    }

    public GetBinary get()
    {
        return new GetBinary();
    }
}
