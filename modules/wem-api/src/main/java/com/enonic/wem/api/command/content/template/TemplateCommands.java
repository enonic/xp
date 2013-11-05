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

    public UpdateTemplate update()
    {
        return new UpdateTemplate();
    }

    public DeleteTemplate delete()
    {
        return new DeleteTemplate();
    }

    public GetTemplate get()
    {
        return new GetTemplate();
    }
}
