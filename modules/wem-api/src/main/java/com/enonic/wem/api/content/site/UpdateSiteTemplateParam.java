package com.enonic.wem.api.content.site;


import com.google.common.base.Preconditions;

public final class UpdateSiteTemplateParam
{
    private SiteTemplateKey key;

    private SiteTemplateEditor editor;

    public UpdateSiteTemplateParam key( final SiteTemplateKey value )
    {
        this.key = value;
        return this;
    }

    public UpdateSiteTemplateParam editor( final SiteTemplateEditor value )
    {
        this.editor = value;
        return this;
    }

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
