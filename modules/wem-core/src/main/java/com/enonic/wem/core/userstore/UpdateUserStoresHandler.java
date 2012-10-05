package com.enonic.wem.core.userstore;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.userstore.UpdateUserStores;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.editor.UserStoreEditor;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;

@Component
public class UpdateUserStoresHandler
    extends AbstractUserStoreHandler<UpdateUserStores>
{
    private AccountDao accountDao;

    public UpdateUserStoresHandler()
    {
        super( UpdateUserStores.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateUserStores command )
        throws Exception
    {
        final UserStoreNames names = command.getNames();
        final UserStoreEditor editor = command.getEditor();
        int userStoresUpdated = 0;

        for ( UserStoreName name : names )
        {
            final Session session = context.getJcrSession();
            final UserStore userStore = retrieveUserStore( session, name );

            if ( userStore != null )
            {
                final boolean flag = editor.edit( userStore );
                if ( flag )
                {
                    updateUserStore( session, userStore );
                    userStoresUpdated++;
                }
            }
        }

        command.setResult( userStoresUpdated );
    }

    private UserStore retrieveUserStore( final Session session, final UserStoreName userStoreName )
        throws Exception
    {
        final UserStore userStore = accountDao.getUserStore( session, userStoreName, true, false );
        userStore.setConnector( getUserStoreConnector( userStoreName ) );
        final AccountKeys administrators = accountDao.getUserStoreAdministrators( session, userStoreName );
        userStore.setAdministrators( administrators );
        return userStore;
    }

    private void updateUserStore( final Session session, final UserStore userStore )
        throws Exception
    {
        accountDao.updateUserStore( session, userStore );
        final AccountKeys administrators = userStore.getAdministrators() == null ? AccountKeys.empty() : userStore.getAdministrators();
        accountDao.setUserStoreAdministrators( session, userStore.getName(), administrators );
    }

    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
