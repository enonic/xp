package com.enonic.wem.admin.rpc.userstore;

import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.api.userstore.statistics.UserStoreStatistics;


public abstract class AbstractUserStoreRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    protected UserStore createUserStore( UserStoreName name, String connector, boolean isDefault, boolean includeAdmins,
                                         boolean includeStats, boolean includeConfig )
    {
        UserStore store = new UserStore( name );

        store.setConnectorName( connector );
        store.setDefaultStore( isDefault );

        store.setAdministrators( includeAdmins ? AccountKeys.from( "user:enonic:admin1" ) : AccountKeys.empty() );

        if ( includeStats )
        {
            final UserStoreStatistics statistics = new UserStoreStatistics();
            statistics.setNumGroups( 33 );
            statistics.setNumRoles( 5 );
            statistics.setNumUsers( 57 );
            store.setStatistics( statistics );
        }

        final UserStoreConfig config = new UserStoreConfig();
        if ( includeConfig )
        {
            final UserStoreFieldConfig phoneField = new UserStoreFieldConfig( "phone" );
            phoneField.setReadOnly( true );
            config.addField( phoneField );

            final UserStoreFieldConfig organizationField = new UserStoreFieldConfig( "organization" );
            organizationField.setRequired( true );
            config.addField( organizationField );

            final UserStoreFieldConfig firstNameField = new UserStoreFieldConfig( "firstName" );
            firstNameField.setRemote( true );
            config.addField( firstNameField );
        }
        store.setConfig( config );

        return store;
    }
}
