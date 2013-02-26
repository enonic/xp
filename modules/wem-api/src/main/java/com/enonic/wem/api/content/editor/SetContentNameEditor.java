package com.enonic.wem.api.content.editor;


import com.enonic.wem.api.content.Content;

import static com.enonic.wem.api.content.Content.newContent;

final class SetContentNameEditor
    implements ContentEditor
{
    private String name;

    SetContentNameEditor( final String name )
    {
        this.name = name;
    }

    @Override
    public Content edit( final Content toBeEdited )
        throws Exception
    {
        if ( toBeEdited.getName().equals( name ) )
        {
            return null;
        }

        return newContent( toBeEdited ).
            name( name ).
            build();
    }
}
