package com.enonic.wem.api.command.schema.content;

public class ContentTypeGetCommands
{
    public GetContentType byName()
    {
        return new GetContentType();
    }

    public GetContentTypes byNames()
    {
        return new GetContentTypes();
    }

    public GetAllContentTypes all()
    {
        return new GetAllContentTypes();
    }

    public GetRootContentTypes roots()
    {
        return new GetRootContentTypes();
    }

    public GetChildContentTypes children()
    {
        return new GetChildContentTypes();
    }

}
