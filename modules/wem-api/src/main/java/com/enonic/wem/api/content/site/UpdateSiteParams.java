package com.enonic.wem.api.content.site;


import com.enonic.wem.api.content.ContentId;

public final class UpdateSiteParams
{
    private ContentId content;

    private SiteEditor editor;

    public UpdateSiteParams content( final ContentId value )
    {
        this.content = value;
        return this;
    }

    public UpdateSiteParams editor( final SiteEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public ContentId getContent()
    {
        return content;
    }

    public SiteEditor getEditor()
    {
        return editor;
    }
}
