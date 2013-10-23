package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.form.Form;

import static com.enonic.wem.api.content.Content.newContent;

final class SetFormEditor
    implements ContentEditor
{
    protected final Form form;

    SetFormEditor( final Form form )
    {
        this.form = form;
    }

    @Override
    public Content edit( final Content toBeEdited )
        throws Exception
    {
        if ( toBeEdited.getForm().equals( form ) )
        {
            return null;
        }

        return newContent( toBeEdited ).
            form( form ).
            build();
    }

}
