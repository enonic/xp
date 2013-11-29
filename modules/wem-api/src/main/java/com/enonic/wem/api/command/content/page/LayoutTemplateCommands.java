package com.enonic.wem.api.command.content.page;


public final class LayoutTemplateCommands
{
    public CreateLayoutTemplate create()
    {
        return new CreateLayoutTemplate();
    }

    public UpdateLayoutTemplate update()
    {
        return new UpdateLayoutTemplate();
    }

    public DeleteLayoutTemplate delete()
    {
        return new DeleteLayoutTemplate();
    }

    public GetLayoutTemplateByKey getByKey()
    {
        return new GetLayoutTemplateByKey();
    }

    public GetLayoutTemplatesBySiteTemplate getBySiteTemplate()
    {
        return new GetLayoutTemplatesBySiteTemplate();
    }

}
