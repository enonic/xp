package com.enonic.wem.core.account;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.command.account.CreateAccount;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.search.IndexService;

@Component
public final class CreateAccountHandler
    extends CommandHandler<CreateAccount>
{

    private IndexService indexService;

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
        final Session session = context.getJcrSession();
        account.setCreatedTime( DateTime.now() );
        account.setModifiedTime( DateTime.now() );
        if ( key.isUser() )
        {
            accountDao.createUser( (UserAccount) account, session );
        }
        else if ( key.isGroup() )
        {
            accountDao.createGroup( (GroupAccount) account, session );
        }
        else if ( key.isRole() )
        {
            accountDao.createRole( (RoleAccount) account, context.getJcrSession() );
        }
        session.save();

        this.indexService.indexAccount( account );
        command.setResult( key );
    }


    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }

    @Autowired
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
