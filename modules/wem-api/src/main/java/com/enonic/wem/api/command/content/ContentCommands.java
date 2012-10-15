package com.enonic.wem.api.command.content;


public final class ContentCommands
{
    public CreateContent create()
    {
        return new CreateContent();
    }

    public UpdateContent update()
    {
        return new UpdateContent();
    }

    public GetContents get()
    {
        return new GetContents();
    }
}
