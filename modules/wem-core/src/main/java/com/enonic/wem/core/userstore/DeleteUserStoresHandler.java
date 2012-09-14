package com.enonic.wem.core.userstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.userstore.DeleteUserStores;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.user.DeleteUserStoreCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public class DeleteUserStoresHandler
    extends CommandHandler<DeleteUserStores>
{
    private UserStoreDao userStoreDao;

    private UserStoreService userStoreService;

    private UserDao userDao;

    public DeleteUserStoresHandler()
    {
        super( DeleteUserStores.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteUserStores command )
        throws Exception
    {
        final UserStoreNames userStoreNames = command.getNames();
        int userStoresDeleted = 0;
        for ( UserStoreName userStoreName : userStoreNames )
        {
            if ( deleteUserStore( userStoreName ) )
            {
                userStoresDeleted++;
            }
        }
        command.setResult( userStoresDeleted );
    }


    private boolean deleteUserStore( UserStoreName userStoreName )
    {
        final UserStoreEntity userStoreEntity = userStoreDao.findByName( userStoreName.toString() );
        if ( userStoreEntity == null )
        {
            return false;
        }
        final UserEntity deleter = userDao.findBuiltInEnterpriseAdminUser(); //TODO get logged in user
        final DeleteUserStoreCommand deleteUserStoreCommand = new DeleteUserStoreCommand();
        deleteUserStoreCommand.setDeleter( deleter.getKey() );
        deleteUserStoreCommand.setKey( userStoreEntity.getKey() );
        userStoreService.deleteUserStore( deleteUserStoreCommand );
        return true;
    }

    @Autowired
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }

    @Autowired
    public void setUserStoreService( final UserStoreService userStoreService )
    {
        this.userStoreService = userStoreService;
    }

    @Autowired
    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }
}
