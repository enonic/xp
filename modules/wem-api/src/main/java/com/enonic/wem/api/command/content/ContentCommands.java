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

    public DeleteContents delete()
    {
        return new DeleteContents();
    }

    public GetContents get()
    {
        return new GetContents();
    }

    public GetChildContent getChildren()
    {
        return new GetChildContent();
    }

    public GetContentTree getTree()
    {
        return new GetContentTree();
    }

    public ValidateRootDataSet validate()
    {
        return new ValidateRootDataSet();
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
}
