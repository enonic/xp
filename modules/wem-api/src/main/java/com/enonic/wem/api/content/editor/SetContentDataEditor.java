package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.RootDataSet;

import static com.enonic.wem.api.content.Content.newContent;

final class SetContentDataEditor
    implements ContentEditor
{
    protected final RootDataSet rootDataSet;

    SetContentDataEditor( final RootDataSet rootDataSet )
    {
        this.rootDataSet = rootDataSet;
    }

    @Override
    public Content edit( final Content toBeEdited )
        throws Exception
    {
        if ( toBeEdited.getRootDataSet().equals( rootDataSet ) )
        {
            return null;
        }

        return newContent( toBeEdited ).
            rootDataSet( rootDataSet ).
            build();
    }

}
