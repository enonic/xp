package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.RootDataSet;

import static com.enonic.wem.api.content.Content.newContent;

final class SetContentDataEditor
    implements ContentEditor
{
    protected final RootDataSet source;

    SetContentDataEditor( final RootDataSet source )
    {
        this.source = source;
    }

    @Override
    public Content edit( final Content toBeEdited )
        throws Exception
    {

        final Content afterEdit = newContent( toBeEdited ).
            rootDataSet( source ).
            build();

        return toBeEdited.getRootDataSet().equals( afterEdit.getRootDataSet() ) ? null : afterEdit;
    }

}
