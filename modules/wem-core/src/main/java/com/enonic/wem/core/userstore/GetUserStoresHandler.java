package com.enonic.wem.core.userstore;

import java.util.List;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.userstore.GetUserStores;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.CommandContext;

@Component
public class GetUserStoresHandler
    extends AbstractUserStoreHandler<GetUserStores>
{
    private AccountDao accountDao;

    public GetUserStoresHandler()
    {
        super( GetUserStores.class );
    }

    @Override
    public void handle( final CommandContext context, final GetUserStores command )
        throws Exception
    {
        final boolean includeConfig = command.isIncludeConfig();
        final boolean includeConnector = command.isIncludeConnector();
        final boolean includeStatistics = command.isIncludeStatistics();
        final UserStoreNames userStoreNames = command.getNames();

        final List<UserStore> userStoreList = Lists.newArrayList();
        final Session session = context.getJcrSession();
        for ( UserStoreName userStoreName : userStoreNames )
        {
            final UserStore userStore = accountDao.getUserStore( userStoreName, includeConfig, includeStatistics, session );
            if ( includeConnector )
            {
                userStore.setConnector( getUserStoreConnector( userStoreName ) );
            }
            final AccountKeys administrators = accountDao.getUserStoreAdministrators( userStoreName, session );
            userStore.setAdministrators( administrators );
            userStoreList.add( userStore );
        }

        command.setResult( UserStores.from( userStoreList ) );
    }

    @Autowired
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }
}
