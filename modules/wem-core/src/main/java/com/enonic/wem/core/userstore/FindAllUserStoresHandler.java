package com.enonic.wem.core.userstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.userstore.FindAllUserStores;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public class FindAllUserStoresHandler
    extends CommandHandler<FindAllUserStores>
{
    private AccountDao accountDao;

    public FindAllUserStoresHandler()
    {
        super( FindAllUserStores.class );
    }

    @Override
    public void handle( final CommandContext context, final FindAllUserStores command )
        throws Exception
    {
        final UserStoreNames userStoreNames = accountDao.getUserStoreNames( context.getJcrSession() );
        command.setResult( UserStoreNames.from( userStoreNames ) );
    }

    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
