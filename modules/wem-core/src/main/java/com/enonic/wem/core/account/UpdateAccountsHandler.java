package com.enonic.wem.core.account;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.enonic.wem.core.search.account.AccountSearchService;

@Component
public final class UpdateAccountsHandler
    extends CommandHandler<UpdateAccounts>
{
    private AccountSearchService searchService;

    private AccountDao accountDao;

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
                return accountDao.findUser( session, account, true, true );
            case GROUP:
                return accountDao.findGroup( session, account, true );
            default:
                return accountDao.findRole( session, account, true );
        }
    }

    private void updateAccount( final Session session, final Account account )
        throws Exception
    {
        switch ( account.getKey().getType() )
        {
            case USER:
                accountDao.updateUser( session, (UserAccount) account );
                break;
            case GROUP:
                accountDao.updateGroup( session, (GroupAccount) account );
                break;
            case ROLE:
                accountDao.updateRole( session, (RoleAccount) account );
                break;
        }
        this.searchService.index( account );
    }

    @Autowired
    public void setSearchService( final AccountSearchService searchService )
    {
        this.searchService = searchService;
    }

    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
