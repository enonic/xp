package com.enonic.wem.api.userstore.editor;

import com.enonic.wem.api.userstore.UserStore;

public interface UserStoreEditor
{
    public boolean edit( UserStore userStore )
        throws Exception;
}
