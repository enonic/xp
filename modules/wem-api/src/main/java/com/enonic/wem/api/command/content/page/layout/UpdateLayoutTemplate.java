package com.enonic.wem.api.command.content.page.layout;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateEditor;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public final class UpdateLayoutTemplate
    extends Command<LayoutTemplate>
{
    private SiteTemplateKey siteTemplateKey;

    private LayoutTemplateKey key;

    private LayoutTemplateEditor editor;

    public UpdateLayoutTemplate siteTemplateKey( final SiteTemplateKey siteTemplateKey )
    {
        this.siteTemplateKey = siteTemplateKey;
        return this;
    }

    public UpdateLayoutTemplate key( final LayoutTemplateKey key )
    {
        this.key = key;
        return this;
    }

    public UpdateLayoutTemplate editor( final LayoutTemplateEditor editor )
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

    public LayoutTemplateKey getKey()
    {
        return key;
    }

    public LayoutTemplateEditor getEditor()
    {
        return editor;
    }
}
