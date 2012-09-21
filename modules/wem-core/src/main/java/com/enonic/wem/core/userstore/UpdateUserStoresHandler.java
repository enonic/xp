package com.enonic.wem.core.userstore;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.userstore.UpdateUserStores;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.editor.UserStoreEditor;
import com.enonic.wem.core.command.CommandContext;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UpdateUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;

@Component
public class UpdateUserStoresHandler
    extends UserStoreHandler<UpdateUserStores>
{
    public UpdateUserStoresHandler()
    {
        super( UpdateUserStores.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateUserStores command )
        throws Exception
    {
        UserStoreNames names = command.getNames();
        UserStoreEditor editor = command.getEditor();
        int userStoresUpdated = 0;

        for ( UserStoreName name : names )
        {
            UserStore userStore = retrieveUserStore( name );
            if ( userStore != null )
            {
                final boolean flag = editor.edit( userStore );
                if ( flag )
                {
                    updateUserStore( userStore );
                    userStoresUpdated++;
                }
            }
        }

        command.setResult( userStoresUpdated );
    }

    private UserStore retrieveUserStore( UserStoreName name )
    {
        UserStoreEntity userStoreEntity = userStoreDao.findByName( name.toString() );
        UserStore userStore = new UserStore( name );
        userStore.setConnector( getUserStoreConnector( userStoreEntity ) );
        userStore.setDefaultStore( userStoreEntity.isDefaultUserStore() );
        userStore.setConfig( getUserStoreConfig( userStoreEntity.getConfig() ) );
        userStore.setConnectorName( userStoreEntity.getConnectorName() );
        userStore.setAdministrators( getUserStoreAdministrators( userStoreEntity ) );
        return userStore;
    }

    private void updateUserStore( UserStore userStore )
    {
        UpdateUserStoreCommand command = new UpdateUserStoreCommand();
        command.setConnectorName( userStore.getConnectorName() );
        command.setName( userStore.getName().toString() );
        command.setConfig( convertToOldConfig( userStore.getConfig() ) );
        UserEntity updater = userDao.findBuiltInEnterpriseAdminUser(); //TODO get login user
        command.setUpdater( updater.getKey() );
        userStoreService.updateUserStore( command );
        UserStoreKey usKey = userStoreDao.findByName( userStore.getName().toString() ).getKey();
        updateUserstoreAdministrators( updater, usKey, userStore.getAdministrators() );
    }

}
