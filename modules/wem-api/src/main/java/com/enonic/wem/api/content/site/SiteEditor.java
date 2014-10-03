package com.enonic.wem.api.content.site;

import com.enonic.wem.api.content.editor.ContentEditor;

public interface SiteEditor
    extends ContentEditor<Site>
{
    public Site.SiteEditBuilder edit( Site toBeEdited );
}
