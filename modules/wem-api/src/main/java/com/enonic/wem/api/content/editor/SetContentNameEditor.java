package com.enonic.wem.api.content.editor;


import com.enonic.wem.api.content.Content;

final class SetContentNameEditor
    implements ContentEditor
{
    private String source;

    SetContentNameEditor( final String source )
    {
        this.source = source;
    }

    @Override
    public boolean edit( final Content content )
        throws Exception
    {
        content.setName( source );
        return false;
    }
}
