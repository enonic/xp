package com.enonic.wem.migrate.jcr;

public interface DatabaseAccountsLoader
{
    void loadUserStores( ImportDataCallbackHandler handler );

    void loadUsers( ImportDataCallbackHandler handler );

    void loadGroups( ImportDataCallbackHandler handler );

    void loadMemberships( ImportDataCallbackHandler handler );
}
