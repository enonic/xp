package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.template.GetTemplate;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateId;

public class GetPageTemplate
    extends GetTemplate<PageTemplate>
{
    private PageTemplateId id;

    public GetPageTemplate( final PageTemplateId id )
    {
        super( id );
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
