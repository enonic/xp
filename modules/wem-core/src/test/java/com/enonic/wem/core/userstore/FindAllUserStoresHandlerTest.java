package com.enonic.wem.core.userstore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.userstore.FindAllUserStores;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class FindAllUserStoresHandlerTest
    extends AbstractCommandHandlerTest
{
    private AccountDao accountDao;

    private FindAllUserStoresHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );

        handler = new FindAllUserStoresHandler();
        handler.setContext( this.context );
        handler.setAccountDao( accountDao );
    }

    @Test
    public void testFindAllUserStores()
        throws Exception
    {
        final UserStoreNames userStores = UserStoreNames.from( UserStoreName.from( "default" ), UserStoreName.from( "enonic" ) );
        Mockito.when( accountDao.getUserStoreNames( session ) ).thenReturn( userStores );

        final FindAllUserStores command = Commands.userStore().findAll();

        this.handler.setCommand( command );
        this.handler.handle();

        UserStoreNames userStoreNames = command.getResult();

        assertEquals( UserStoreNames.from( "default", "enonic" ), userStoreNames );
    }

}
