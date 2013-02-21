package com.enonic.wem.core.userstore;

import javax.jcr.Session;

import javax.inject.Inject;
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
        final Session session = context.getJcrSession();
        final UserStoreNames userStoreNames = command.getNames();
        int userStoresDeleted = 0;
        for ( UserStoreName userStoreName : userStoreNames )
        {
            if ( accountDao.deleteUserStore( userStoreName, session ) )
            {
                userStoresDeleted++;
            }
        }
        if ( userStoresDeleted > 0 )
        {
            session.save();
        }
        command.setResult( userStoresDeleted );
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
