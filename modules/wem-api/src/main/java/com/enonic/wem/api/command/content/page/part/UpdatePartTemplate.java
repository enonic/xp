package com.enonic.wem.api.command.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateEditor;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public final class UpdatePartTemplate
    extends Command<PartTemplate>
{
    private SiteTemplateKey siteTemplateKey;

    private PartTemplateKey key;

    private PartTemplateEditor editor;

    public UpdatePartTemplate siteTemplateKey( final SiteTemplateKey siteTemplateKey )
    {
        this.siteTemplateKey = siteTemplateKey;
        return this;
    }

    public UpdatePartTemplate key( final PartTemplateKey key )
    {
        this.key = key;
        return this;
    }

    public UpdatePartTemplate editor( final PartTemplateEditor editor )
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

    public PartTemplateKey getKey()
    {
        return key;
    }

    public PartTemplateEditor getEditor()
    {
        return editor;
    }
}
