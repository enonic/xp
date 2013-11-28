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

    public DeleteContent delete()
    {
        return new DeleteContent();
    }

    public ContentGetCommands get()
    {
        return new ContentGetCommands();
    }

    public GetRootContent getRoots()
    {
        return new GetRootContent();
    }

    public GetChildContent getChildren()
    {
        return new GetChildContent();
    }

    public ValidateContentData validate()
    {
        return new ValidateContentData();
    }

    public GetContentVersion getVersion()
    {
        return new GetContentVersion();
    }

    public GenerateContentName generateContentName()
    {
        return new GenerateContentName();
    }

    public RenameContent rename()
    {
        return new RenameContent();
    }

    public FindContent find()
    {
        return new FindContent();
    }

}
