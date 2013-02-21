package com.enonic.wem.core.account;

import javax.jcr.Session;

import org.joda.time.DateTime;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.editor.AccountEditor;
import com.enonic.wem.api.command.account.UpdateAccounts;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.search.IndexService;

@Component
public final class UpdateAccountsHandler
    extends CommandHandler<UpdateAccounts>
{
    // private AccountSearchService searchService;

    private AccountDao accountDao;

    private IndexService indexService;

    public UpdateAccountsHandler()
    {
        super( UpdateAccounts.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateAccounts command )
        throws Exception
    {
        final AccountKeys accountKeys = command.getKeys();
        final AccountEditor editor = command.getEditor();
        final Session session = context.getJcrSession();

        int accountsUpdated = 0;
        for ( AccountKey accountKey : accountKeys )
        {
            final Account account = retrieveAccount( session, accountKey );
            if ( account != null )
            {
                final boolean flag = editor.edit( account );
                if ( flag )
                {
                    updateAccount( session, account );
                    accountsUpdated++;
                }
            }
        }

        session.save();

        command.setResult( accountsUpdated );
    }

    private Account retrieveAccount( final Session session, final AccountKey account )
        throws Exception
    {
        switch ( account.getType() )
        {
            case USER:
                return accountDao.findUser( account.asUser(), true, true, session );
            case GROUP:
                return accountDao.findGroup( account.asGroup(), true, session );
            default:
                return accountDao.findRole( account.asRole(), true, session );
        }
    }

    private void updateAccount( final Session session, final Account account )
        throws Exception
    {
        account.setModifiedTime( DateTime.now() );
        switch ( account.getKey().getType() )
        {
            case USER:
                accountDao.updateUser( (UserAccount) account, session );
                break;
            case GROUP:
                accountDao.updateGroup( (GroupAccount) account, session );
                break;
            case ROLE:
                accountDao.updateRole( (RoleAccount) account, session );
                break;
        }

        this.indexService.indexAccount( account );
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
