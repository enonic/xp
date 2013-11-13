package com.enonic.wem.api.content.editor;


import com.enonic.wem.api.content.Content;

import static com.enonic.wem.api.content.Content.newContent;

final class SetContentDisplayNameEditor
    implements ContentEditor
{
    private String displayName;

    SetContentDisplayNameEditor( final String displayName )
    {
        this.displayName = displayName;
    }

    @Override
    public Content edit( final Content toBeEdited )
    {
        if ( toBeEdited.getDisplayName().equals( displayName ) )
        {
            return null;
        }

        return newContent( toBeEdited ).
            displayName( displayName ).
            build();
    }

}
