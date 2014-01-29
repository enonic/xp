package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateEditor;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public final class UpdatePageTemplate
    extends Command<PageTemplate>
{
    private SiteTemplateKey siteTemplateKey;

    private PageTemplateKey key;

    private PageTemplateEditor editor;

    public UpdatePageTemplate siteTemplateKey( final SiteTemplateKey siteTemplateKey )
    {
        this.siteTemplateKey = siteTemplateKey;
        return this;
    }

    public UpdatePageTemplate key( final PageTemplateKey key )
    {
        this.key = key;
        return this;
    }

    public UpdatePageTemplate editor( final PageTemplateEditor editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( siteTemplateKey, "siteTemplateKey is required" );
        Preconditions.checkNotNull( key, "key is required" );
    }

    public SiteTemplateKey getSiteTemplateKey()
    {
        return siteTemplateKey;
    }

    public PageTemplateKey getKey()
    {
        return key;
    }

    public PageTemplateEditor getEditor()
    {
        return editor;
    }
}
