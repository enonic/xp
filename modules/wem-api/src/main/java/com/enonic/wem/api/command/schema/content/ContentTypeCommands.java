package com.enonic.wem.api.command.schema.content;

public final class ContentTypeCommands
{
    public GetContentTypes get()
    {
        return new GetContentTypes();
    }

    public CreateContentType create()
    {
        return new CreateContentType();
    }

    public UpdateContentType update()
    {
        return new UpdateContentType();
    }

    public DeleteContentType delete()
    {
        return new DeleteContentType();
    }

    public GetContentTypeTree getTree()
    {
        return new GetContentTypeTree();
    }

    public GetRootContentTypes getRoots()
    {
        return new GetRootContentTypes();
    }

    public GetChildContentTypes getChildren()
    {
        return new GetChildContentTypes();
    }

    public ValidateContentType validate()
    {
        return new ValidateContentType();
    }
}
