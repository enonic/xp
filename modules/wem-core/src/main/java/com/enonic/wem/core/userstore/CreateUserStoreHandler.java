package com.enonic.wem.core.userstore;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.userstore.CreateUserStore;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.core.command.CommandContext;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreKey;

@Component
public class CreateUserStoreHandler
    extends AbstractUserStoreHandler<CreateUserStore>
{

    public CreateUserStoreHandler()
    {
        super( CreateUserStore.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateUserStore command )
        throws Exception
    {
        UserStore userStore = command.getUserStore();
        UserEntity storer = userDao.findBuiltInEnterpriseAdminUser(); // TODO get logged in user
        StoreNewUserStoreCommand storeCommand = new StoreNewUserStoreCommand();
        storeCommand.setConnectorName( userStore.getConnectorName() );
        storeCommand.setName( userStore.getName().toString() );
        storeCommand.setDefaultStore( userStore.isDefaultStore() );
        storeCommand.setConfig( convertToOldConfig( userStore.getConfig() ) );
        storeCommand.setStorer( storer.getKey() );
        UserStoreKey newUserStore = userStoreService.storeNewUserStore( storeCommand );
//        updateUserstoreAdministrators( storer, newUserStore, userStore.getAdministrators() );
        command.setResult( userStore.getName() );
    }

}
