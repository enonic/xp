package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.content.page.TemplateNames;

public final class GetTemplateCommands
{

    public GetPageTemplate pageTemplate()
    {
        return new GetPageTemplate();
    }

    public GetPartTemplate partTemplate()
    {
        return new GetPartTemplate();
    }

    public GetLayoutTemplate layoutTemplate()
    {
        return new GetLayoutTemplate();
    }

    public GetImageTemplate imageTemplate()
    {
        return new GetImageTemplate();
    }

    public GetTemplates all()
    {
        return new GetTemplates().all();
    }

    public GetTemplates byNames( final TemplateNames templateNames )
    {
        return new GetTemplates().templates( templateNames );
    }
}
