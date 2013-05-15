package com.enonic.wem.migrate.account;

public interface DatabaseAccountsLoader
{
    void loadUserStores( ImportDataCallbackHandler handler )
        throws Exception;

    void loadUsers( ImportDataCallbackHandler handler )
        throws Exception;

    void loadGroups( ImportDataCallbackHandler handler )
        throws Exception;

    void loadMemberships( ImportDataCallbackHandler handler )
        throws Exception;
}
