package com.enonic.wem.core.userstore;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.userstore.CreateUserStore;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public class CreateUserStoreHandler
    extends CommandHandler<CreateUserStore>
{
    private AccountDao accountDao;

    public CreateUserStoreHandler()
    {
        super( CreateUserStore.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateUserStore command )
        throws Exception
    {
        final UserStore userStore = command.getUserStore();
        final Session session = context.getJcrSession();
        accountDao.createUserStore( userStore, session );

        final AccountKeys administrators = userStore.getAdministrators() == null ? AccountKeys.empty() : userStore.getAdministrators();
        accountDao.setUserStoreAdministrators( userStore.getName(), administrators, session );

        session.save();
        command.setResult( userStore.getName() );
    }

    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
