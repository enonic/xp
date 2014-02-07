package com.enonic.wem.api.command.content.page;


public final class PageTemplateCommands
{

    public CreatePageTemplate create()
    {
        return new CreatePageTemplate();
    }

    public DeletePageTemplate delete()
    {
        return new DeletePageTemplate();
    }

    public GetPageTemplateByKey getByKey()
    {
        return new GetPageTemplateByKey();
    }

    public GetPageTemplatesBySiteTemplate getBySiteTemplate()
    {
        return new GetPageTemplatesBySiteTemplate();
    }
}
