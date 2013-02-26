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
        throws Exception
    {
        final Content afterEdit = newContent( toBeEdited ).
            displayName( displayName ).
            build();
        return toBeEdited.getDisplayName().equals( afterEdit.getDisplayName() ) ? null : afterEdit;
    }

}
