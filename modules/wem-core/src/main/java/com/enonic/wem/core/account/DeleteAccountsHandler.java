package com.enonic.wem.core.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void handle( final CommandContext context, final DeleteAccounts command )
        throws Exception
    {
        final AccountKeys accountKeys = command.getKeys();

        int accountsDeleted = 0;
        for ( AccountKey accountKey : accountKeys )
        {
            if ( this.accountDao.delete( context.getJcrSession(), accountKey ) )
            {
                this.searchService.deleteIndex( accountKey.toString() );
                accountsDeleted++;
            }
        }
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
