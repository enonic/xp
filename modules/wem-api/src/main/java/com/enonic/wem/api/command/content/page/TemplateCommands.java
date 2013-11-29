package com.enonic.wem.api.command.content.page;


public final class TemplateCommands
{

    public PageTemplateCommands page()
    {
        return new PageTemplateCommands();
    }

    public ImageTemplateCommands image()
    {
        return new ImageTemplateCommands();
    }

    public PartTemplateCommands part()
    {
        return new PartTemplateCommands();
    }

    public LayoutTemplateCommands layout()
    {
        return new LayoutTemplateCommands();
    }
}
