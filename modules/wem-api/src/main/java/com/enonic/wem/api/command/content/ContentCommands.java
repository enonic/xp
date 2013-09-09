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

    public GetContents get()
    {
        return new GetContents();
    }

    public GetRootContent getRoot()
    {
        return new GetRootContent();
    }

    public GetChildContent getChildren()
    {
        return new GetChildContent();
    }

    public GetContentTree getTree()
    {
        return new GetContentTree();
    }

    public ValidateContentData validate()
    {
        return new ValidateContentData();
    }

    public GetContentVersionHistory getVersionHistory()
    {
        return new GetContentVersionHistory();
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
