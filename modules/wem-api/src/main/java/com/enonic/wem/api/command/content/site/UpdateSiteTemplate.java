package com.enonic.wem.api.command.content.site;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateEditor;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class UpdateSiteTemplate
    extends Command<SiteTemplate>
{
    private SiteTemplateKey key;

    private SiteTemplateEditor editor;

    public UpdateSiteTemplate key( final SiteTemplateKey value )
    {
        this.key = value;
        return this;
    }

    public UpdateSiteTemplate editor( final SiteTemplateEditor value )
    {
        this.editor = value;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
        Preconditions.checkNotNull( key, "editor is required" );
    }

    public SiteTemplateKey getKey()
    {
        return key;
    }

    public SiteTemplateEditor getEditor()
    {
        return editor;
    }
}
