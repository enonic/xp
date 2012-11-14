package com.enonic.wem.api.content.editor;


import com.enonic.wem.api.content.Content;

final class SetContentDisplayNameEditor
    implements ContentEditor
{
    private String displayName;

    SetContentDisplayNameEditor( final String displayName )
    {
        this.displayName = displayName;
    }

    @Override
    public boolean edit( final Content content )
        throws Exception
    {
        content.setDisplayName( displayName );
        return false;
    }
}
