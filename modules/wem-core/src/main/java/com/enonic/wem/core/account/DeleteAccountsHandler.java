package com.enonic.wem.core.account;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.account.DeleteAccounts;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.search.account.AccountSearchService;

@Component
public class DeleteAccountsHandler
    extends CommandHandler<DeleteAccounts>
{

    private AccountDao accountDao;

    private AccountSearchService searchService;

    public DeleteAccountsHandler()
    {
        super( DeleteAccounts.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteAccounts command )
        throws Exception
    {
        final AccountKeys accountKeys = command.getKeys();

        int accountsDeleted = 0;
        final Session session = context.getJcrSession();
        for ( AccountKey accountKey : accountKeys )
        {
            if ( this.accountDao.delete( context.getJcrSession(), accountKey ) )
            {
                this.searchService.deleteIndex( accountKey.toString() );
                accountsDeleted++;
            }
        }
        session.save();

        command.setResult( accountsDeleted );
    }

    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }

    @Autowired
    public void setSearchService( final AccountSearchService searchService )
    {
        this.searchService = searchService;
    }
}
