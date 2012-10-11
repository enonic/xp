package com.enonic.wem.api.userstore.editor;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.userstore.UserStore;

final class SetUserStoreAdministratorsEditor
    implements UserStoreEditor
{
    private final AccountKeys administrators;

    public SetUserStoreAdministratorsEditor( final AccountKeys administrators )
    {
        this.administrators = administrators;
    }

    @Override
    public boolean edit( final UserStore userStore )
        throws Exception
    {
        userStore.setAdministrators( administrators );
        return true;
    }

}
