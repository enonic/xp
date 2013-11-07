package com.enonic.wem.api.command.schema.content;

public class ContentTypeGetCommands
{
    public GetAllContentTypes all()
    {
        return new GetAllContentTypes();
    }

    public GetContentType byName()
    {
        return new GetContentType();
    }

    public GetContentTypes byNames()
    {
        return new GetContentTypes();
    }

    public GetRootContentTypes getRoots()
    {
        return new GetRootContentTypes();
    }

    public GetChildContentTypes children()
    {
        return new GetChildContentTypes();
    }

}
