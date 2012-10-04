package com.enonic.wem.core.userstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.userstore.DeleteUserStores;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public class DeleteUserStoresHandler
    extends CommandHandler<DeleteUserStores>
{
    private AccountDao accountDao;

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
            if ( accountDao.deleteUserStore( context.getJcrSession(), userStoreName ) )
            {
                userStoresDeleted++;
            }
        }
        command.setResult( userStoresDeleted );
    }

    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
