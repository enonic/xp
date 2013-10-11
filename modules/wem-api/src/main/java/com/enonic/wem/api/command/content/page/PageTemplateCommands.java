package com.enonic.wem.api.command.content.page;


public final class PageTemplateCommands
{
    private final PageTemplateGetCommands getCommands = new PageTemplateGetCommands();

    public CreatePageTemplate create()
    {
        return new CreatePageTemplate();
    }

    public PageTemplateGetCommands get()
    {
        return getCommands;
    }
}
