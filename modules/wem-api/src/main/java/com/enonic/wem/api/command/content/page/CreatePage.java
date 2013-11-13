package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.data.RootDataSet;

public class CreatePage
    extends Command<Content>
{
    private ContentId content;

    private PageTemplateName pageTemplate;

    private RootDataSet config;

    public CreatePage content( ContentId value )
    {
        this.content = value;
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

    public ContentId getContent()
    {
        return content;
    }

    public PageTemplateName getPageTemplate()
    {
        return pageTemplate;
    }

    public RootDataSet getConfig()
    {
        return config;
    }
}
