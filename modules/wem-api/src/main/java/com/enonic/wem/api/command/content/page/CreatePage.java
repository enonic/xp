package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.data.RootDataSet;

public class CreatePage
    extends Command<Page>
{
    private ContentId contentId;

    private PageTemplateName pageTemplate;

    private RootDataSet config;

    public CreatePage content( ContentId value )
    {
        this.contentId = value;
        return this;
    }

    public CreatePage pageTemplate( PageTemplateName value )
    {
        this.pageTemplate = value;
        return this;
    }

    public CreatePage config( RootDataSet value )
    {
        this.config = value;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
