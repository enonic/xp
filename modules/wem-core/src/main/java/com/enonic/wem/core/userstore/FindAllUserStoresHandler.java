package com.enonic.wem.core.userstore;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.userstore.FindAllUserStores;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public class FindAllUserStoresHandler
    extends CommandHandler<FindAllUserStores>
{
    private UserStoreDao userStoreDao;

    public FindAllUserStoresHandler()
    {
        super( FindAllUserStores.class );
    }

    @Override
    public void handle( final CommandContext context, final FindAllUserStores command )
        throws Exception
    {
        List<UserStoreEntity> userStores = userStoreDao.findAll();
        List<UserStoreName> userStoreNames = new ArrayList<>();
        for ( UserStoreEntity userStore : userStores )
        {
            userStoreNames.add( UserStoreName.from( userStore.getName() ) );
        }
        command.setResult( UserStoreNames.from( userStoreNames ) );
    }

    @Autowired
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }
}
