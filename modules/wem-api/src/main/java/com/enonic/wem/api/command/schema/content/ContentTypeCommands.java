package com.enonic.wem.api.command.schema.content;

public final class ContentTypeCommands
{
    public ContentTypeGetCommands get()
    {
        return new ContentTypeGetCommands();
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

    public ValidateContentType validate()
    {
        return new ValidateContentType();
    }
}
