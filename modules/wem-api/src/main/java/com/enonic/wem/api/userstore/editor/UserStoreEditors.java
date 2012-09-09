package com.enonic.wem.api.userstore.editor;

import com.enonic.wem.api.userstore.UserStore;

public abstract class UserStoreEditors
{
    public static UserStoreEditor composite( final UserStoreEditor... editors )
    {
        return new CompositeEditor( editors );
    }

    public static UserStoreEditor setUserStore( final UserStore userStore )
    {
        return new SetUserStoreEditor( userStore );
    }
}
