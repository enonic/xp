package com.enonic.wem.core.userstore;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.UpdateResult;
import com.enonic.wem.api.command.userstore.UpdateUserStore;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.editor.UserStoreEditor;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;


public class UpdateUserStoreHandler
    extends AbstractUserStoreHandler<UpdateUserStore>
{
    private AccountDao accountDao;

    @Override
    public void handle( final CommandContext context, final UpdateUserStore command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final UserStoreName name = command.getName();
        final UserStoreEditor editor = command.getEditor();

        final UserStore userStore = retrieveUserStore( session, name );

        UpdateResult updateResult = UpdateResult.notUpdated();
        if ( userStore != null )
        {
            final boolean updated = editor.edit( userStore );
            if ( updated )
            {
                updateUserStore( session, userStore );
                updateResult = UpdateResult.updated();
            }
        }
        else
        {
            updateResult = UpdateResult.failure( "User store [{0}] not found", name.toString() );
        }

        if ( updateResult.isUpdated() )
        {
            session.save();
        }
        command.setResult( updateResult );
    }

    private UserStore retrieveUserStore( final Session session, final UserStoreName userStoreName )
        throws Exception
    {
        final UserStore userStore = accountDao.getUserStore( userStoreName, true, false, session );
        if ( userStore != null )
        {
            userStore.setConnector( getUserStoreConnector( userStoreName ) );
            final AccountKeys administrators = accountDao.getUserStoreAdministrators( userStoreName, session );
            userStore.setAdministrators( administrators );
        }
        return userStore;
    }

    private void updateUserStore( final Session session, final UserStore userStore )
        throws Exception
    {
        accountDao.updateUserStore( userStore, session );
        final AccountKeys administrators = userStore.getAdministrators() == null ? AccountKeys.empty() : userStore.getAdministrators();
        accountDao.setUserStoreAdministrators( userStore.getName(), administrators, session );
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
