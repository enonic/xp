package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.form.Form;

public abstract class ContentEditors
{
    public static ContentEditor composite( final ContentEditor... editors )
    {
        return new CompositeEditor( editors );
    }

    public static ContentEditor setForm( final Form form )
    {
        return new SetFormEditor( form );
    }

    public static ContentEditor setContentData( final ContentData contentData )
    {
        return new SetContentDataEditor( contentData );
    }

    public static ContentEditor setContentName( final String name )
    {
        return new SetContentNameEditor( name );
    }

    public static ContentEditor setContentDisplayName( final String displayName )
    {
        return new SetContentDisplayNameEditor( displayName );
    }
}
