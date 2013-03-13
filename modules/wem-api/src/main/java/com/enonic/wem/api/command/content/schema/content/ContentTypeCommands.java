package com.enonic.wem.api.command.content.schema.content;

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

    public DeleteContentTypes delete()
    {
        return new DeleteContentTypes();
    }

    public GetContentTypeTree getTree()
    {
        return new GetContentTypeTree();
    }

    public ValidateContentType validate()
    {
        return new ValidateContentType();
    }
}
