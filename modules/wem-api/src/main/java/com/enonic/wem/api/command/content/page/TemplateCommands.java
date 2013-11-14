package com.enonic.wem.api.command.content.page;


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

    public DeleteTemplate deleteTemplate()
    {
        return new DeleteTemplate();
    }

    public GetTemplateCommands get()
    {
        return new GetTemplateCommands();
    }
}
