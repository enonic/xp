package com.enonic.wem.api.userstore.editor;

import com.enonic.wem.api.userstore.UserStore;

final class SetUserStoreEditor
    implements UserStoreEditor
{
    protected final UserStore source;

    public SetUserStoreEditor( final UserStore source )
    {
        this.source = source;
    }

    @Override
    public boolean edit( final UserStore userStore )
        throws Exception
    {
        return edit( this.source, userStore );
    }

    private static boolean edit( final UserStore source, final UserStore target )
        throws Exception
    {
        return true;
    }
}
