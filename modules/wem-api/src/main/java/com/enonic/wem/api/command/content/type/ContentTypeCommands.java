package com.enonic.wem.api.command.content.type;

import com.enonic.wem.api.content.type.ContentType;

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

    public UpdateContentTypes update()
    {
        return new UpdateContentTypes();
    }

    public DeleteContentTypes delete()
    {
        return new DeleteContentTypes();
    }

    public GetContentTypeTree getTree()
    {
        return new GetContentTypeTree();
    }

    public ValidateContentType validate( final ContentType contentType )
    {
        return new ValidateContentType( contentType );
    }
}
