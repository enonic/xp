package com.enonic.wem.api.content.editor;


import com.enonic.wem.api.content.Content;

import static com.enonic.wem.api.content.Content.newContent;

final class SetContentNameEditor
    implements ContentEditor
{
    private String source;

    SetContentNameEditor( final String source )
    {
        this.source = source;
    }

    @Override
    public Content edit( final Content toBeEdited )
        throws Exception
    {
        final Content afterEdit = newContent( toBeEdited ).
            name( source ).
            build();

        return toBeEdited.getName().equals( afterEdit.getName() ) ? null : afterEdit;
    }
}
