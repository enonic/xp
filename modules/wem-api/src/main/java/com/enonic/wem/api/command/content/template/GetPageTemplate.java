package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;

public class GetPageTemplate
    extends Command<PageTemplate>
{
    private PageTemplateName name;

    public GetPageTemplate()
    {
    }

    public GetPageTemplate templateName( final PageTemplateName name )
    {
        this.name = name;
        return this;
    }

    public PageTemplateName getId()
    {
        return name;
    }

    @Override
    public void validate()
    {

    }
}
