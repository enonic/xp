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
    public Content edit( final Content content )
        throws Exception
    {
        final Content updated = newContent( content ).
            name( source ).
            build();
        return updated;
    }
}
