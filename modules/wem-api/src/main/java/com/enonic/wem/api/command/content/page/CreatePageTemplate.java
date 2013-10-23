package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.PageTemplateId;
import com.enonic.wem.api.data.RootDataSet;

public class CreatePageTemplate
    extends Command<CreatePageResult>
{
    private ContentId contentId;

    private PageTemplateId pageTemplate;

    private RootDataSet config;

    public CreatePageTemplate content( ContentId value )
    {
        this.contentId = value;
        return this;
    }

    public CreatePageTemplate pageTemplate( PageTemplateId value )
    {
        this.pageTemplate = value;
        return this;
    }

    public CreatePageTemplate config( RootDataSet value )
    {
        this.config = value;
        return this;
    }

    @Override
    public void validate()
    {
        // TODO
    }
}
