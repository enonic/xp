package com.enonic.wem.api.command.content.template;


public final class TemplateCommands
{

    public CreatePageTemplate createPageTemplate()
    {
        return new CreatePageTemplate();
    }

    public CreatePartTemplate createPartTemplate()
    {
        return new CreatePartTemplate();
    }

    public CreateLayoutTemplate createLayoutTemplate()
    {
        return new CreateLayoutTemplate();
    }

    public CreateImageTemplate createImageTemplate()
    {
        return new CreateImageTemplate();
    }

    public CreateSiteTemplate createSiteTemplate()
    {
        return new CreateSiteTemplate();
    }

    public UpdatePageTemplate updatePageTemplate()
    {
        return new UpdatePageTemplate();
    }

    public UpdatePartTemplate updatePartTemplate()
    {
        return new UpdatePartTemplate();
    }

    public UpdateLayoutTemplate updateLayoutTemplate()
    {
        return new UpdateLayoutTemplate();
    }

    public UpdateImageTemplate updateImageTemplate()
    {
        return new UpdateImageTemplate();
    }

    public DeleteTemplate delete()
    {
        return new DeleteTemplate();
    }

    public GetSiteTemplates listSiteTemplates()
    {
        return new GetSiteTemplates();
    }

    public GetSiteTemplate getSiteTemplate()
    {
        return new GetSiteTemplate();
    }

    public GetPageTemplate getPageTemplate()
    {
        return new GetPageTemplate();
    }

    public GetPartTemplate getPartTemplate()
    {
        return new GetPartTemplate();
    }

    public GetLayoutTemplate getLayoutTemplate()
    {
        return new GetLayoutTemplate();
    }

    public GetImageTemplate getImageTemplate()
    {
        return new GetImageTemplate();
    }

    public GetTemplates listTemplates()
    {
        return new GetTemplates();
    }
}
