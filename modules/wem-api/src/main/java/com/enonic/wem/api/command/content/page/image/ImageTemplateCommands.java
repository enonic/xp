package com.enonic.wem.api.command.content.page.image;


public final class ImageTemplateCommands
{

    public CreateImageTemplate create()
    {
        return new CreateImageTemplate();
    }

    public UpdateImageTemplate update()
    {
        return new UpdateImageTemplate();
    }

    public DeleteImageTemplate delete()
    {
        return new DeleteImageTemplate();
    }

    public GetImageTemplateByKey getByKey()
    {
        return new GetImageTemplateByKey();
    }

    public GetImageTemplatesBySiteTemplate getBySiteTemplate()
    {
        return new GetImageTemplatesBySiteTemplate();
    }
}
