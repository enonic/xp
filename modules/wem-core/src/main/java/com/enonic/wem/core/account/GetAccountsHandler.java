package com.enonic.wem.core.account;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.command.account.GetAccounts;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public final class GetAccountsHandler
    extends CommandHandler<GetAccounts>
{
    private AccountDao accountDao;

    public GetAccountsHandler()
    {
        super( GetAccounts.class );
    }

    @Override
    public void handle( final CommandContext context, final GetAccounts command )
        throws Exception
    {
        final boolean includeMembers = command.isIncludeMembers();
        final boolean includePhoto = command.isIncludeImage();
        final boolean includeProfile = command.isIncludeProfile();

        final Accounts accounts = fetchAccounts( context.getJcrSession(), command.getKeys(), includeMembers, includePhoto, includeProfile );

        command.setResult( accounts );
    }

    private Accounts fetchAccounts( final Session session, final AccountKeys keys, final boolean includeMembers, final boolean includePhoto,
                                    final boolean includeProfile )
        throws Exception
    {
        final List<Account> accountList = new ArrayList<>();
        for ( AccountKey key : keys )
        {
            Account account = null;
            switch ( key.getType() )
            {
                case USER:
                    account = accountDao.findUser( key.asUser(), includeProfile, includePhoto, session );
                    break;

                case GROUP:
                    account = accountDao.findGroup( key.asGroup(), includeMembers, session );
                    break;

                case ROLE:
                    account = accountDao.findRole( key.asRole(), includeMembers, session );
                    break;
            }
            if ( account != null )
            {
                accountList.add( account );
            }
        }

        return Accounts.from( accountList );
    }

    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
