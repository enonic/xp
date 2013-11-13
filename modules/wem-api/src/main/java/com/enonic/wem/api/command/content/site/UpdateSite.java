package com.enonic.wem.api.command.content.site;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.site.SiteEditor;

public class UpdateSite
    extends Command<Content>
{
    private ContentId content;

    private SiteEditor editor;

    public UpdateSite content( final ContentId value )
    {
        this.content = value;
        return this;
    }

    public UpdateSite editor( final SiteEditor editor )
    {
        this.editor = editor;
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

    public SiteEditor getEditor()
    {
        return editor;
    }
}
