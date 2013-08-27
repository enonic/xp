package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.data.data.ContentData;

import static com.enonic.wem.api.content.Content.newContent;

final class SetContentDataEditor
    implements ContentEditor
{
    protected final ContentData contentData;

    SetContentDataEditor( final ContentData contentData )
    {
        this.contentData = contentData;
    }

    @Override
    public Content edit( final Content toBeEdited )
        throws Exception
    {
        if ( toBeEdited.getContentData().equals( contentData ) )
        {
            return null;
        }

        return newContent( toBeEdited ).
            contentData( contentData ).
            build();
    }

}
