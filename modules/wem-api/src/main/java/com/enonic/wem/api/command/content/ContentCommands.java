package com.enonic.wem.api.command.content;


public final class ContentCommands
{
    public CreateContent create()
    {
        return new CreateContent();
    }

    public UpdateContents update()
    {
        return new UpdateContents();
    }

    public GetContents get()
    {
        return new GetContents();
    }

    public GetChildContent getChildren()
    {
        return new GetChildContent();
    }
}
