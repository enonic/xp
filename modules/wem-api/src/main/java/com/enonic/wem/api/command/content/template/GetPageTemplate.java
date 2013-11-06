package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateId;

public class GetPageTemplate
    extends Command<PageTemplate>
{
    private PageTemplateId id;

    public GetPageTemplate()
    {
    }

    public GetPageTemplate templateId( final PageTemplateId id )
    {
        this.id = id;
        return this;
    }

    public PageTemplateId getId()
    {
        return id;
    }

    @Override
    public void validate()
    {

    }
}
