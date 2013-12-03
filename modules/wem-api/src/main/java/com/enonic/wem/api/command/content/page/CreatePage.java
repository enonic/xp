package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.data.RootDataSet;

public class CreatePage
    extends Command<Content>
{
    private ContentId content;

    private PageTemplateKey pageTemplate;

    private RootDataSet config;

    public CreatePage content( ContentId value )
    {
        this.content = value;
        return this;
    }

    public CreatePage pageTemplate( PageTemplateKey value )
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

    public PageTemplateKey getPageTemplate()
    {
        return pageTemplate;
    }

    public RootDataSet getConfig()
    {
        return config;
    }
}
