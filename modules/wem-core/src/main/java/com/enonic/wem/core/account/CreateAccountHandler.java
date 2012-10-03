package com.enonic.wem.core.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.command.account.CreateAccount;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.search.account.AccountSearchService;

@Component
public final class CreateAccountHandler
    extends CommandHandler<CreateAccount>
{
    private AccountSearchService searchService;

    private AccountDao accountDao;

    public CreateAccountHandler()
    {
        super( CreateAccount.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateAccount command )
        throws Exception
    {
        final Account account = command.getAccount();
        final AccountKey key = account.getKey();

        if ( key.isUser() )
        {
            accountDao.createUser( context.getJcrSession(), (UserAccount) account );
        }
        else if ( key.isGroup() )
        {
            accountDao.createGroup( context.getJcrSession(), (GroupAccount) account );
        }
        else if ( key.isRole() )
        {
            throw new IllegalArgumentException( "Roles are built-in and can't be created manually." );
        }

        this.searchService.index( account );
        command.setResult( key );
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
