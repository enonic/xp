package com.enonic.wem.api.userstore.editor;

import com.enonic.wem.api.userstore.UserStore;

final class CompositeEditor
    implements UserStoreEditor
{
    protected final UserStoreEditor[] editors;

    public CompositeEditor( final UserStoreEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public boolean edit( final UserStore userStore )
        throws Exception
    {
        boolean flag = false;
        for ( final UserStoreEditor editor : this.editors )
        {
            final boolean result = editor.edit( userStore );
            flag = flag || result;
        }

        return flag;
    }
}
