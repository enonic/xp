package com.enonic.wem.core.userstore;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.userstore.UpdateUserStores;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.editor.UserStoreEditor;
import com.enonic.wem.core.search.account.AccountSearchService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.connector.config.GroupPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class UpdateUserStoresHandlerTest
    extends AbstractUserStoreHandlerTest
{
    private UserDao userDao;

    private UserStoreDao userStoreDao;

    private UserStoreService userStoreService;

    private SecurityService securityService;

    private AccountSearchService searchService;

    private GroupDao groupDao;

    private UserStoreConnectorManager userStoreConnectorManager;

    private UpdateUserStoresHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        userStoreService = Mockito.mock( UserStoreService.class );
        userDao = Mockito.mock( UserDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
        securityService = Mockito.mock( SecurityService.class );
        searchService = Mockito.mock( AccountSearchService.class );
        groupDao = Mockito.mock( GroupDao.class );
        userStoreConnectorManager = Mockito.mock( UserStoreConnectorManager.class );

        handler = new UpdateUserStoresHandler();
        handler.setUserDao( userDao );
        handler.setUserStoreService( userStoreService );
        handler.setUserStoreDao( userStoreDao );
        handler.setSecurityService( securityService );
        handler.setSearchService( searchService );
        handler.setGroupDao( groupDao );
        handler.setUserStoreConnectorManager( userStoreConnectorManager );
    }

    @Test
    public void testUpdateUserStore()
        throws Exception
    {
        loggedInUser();

        //stub user store config
        final UserStoreConnectorConfig connectorConfig =
            new UserStoreConnectorConfig( "ldap1", "Ldap", UserPolicyConfig.ALL_FALSE, GroupPolicyConfig.ALL_FALSE, false, false );
        Mockito.when( userStoreConnectorManager.getUserStoreConnectorConfig( Mockito.any( UserStoreKey.class ) ) ).thenReturn(
            connectorConfig );

        createUserStore( "default", "1" );
        createUserStore( "enonic", "2" );

        //stub user store administrator group
        GroupEntity usAdmin = createGroup( "2323232", "default", "usAdmin" );
        Mockito.when( groupDao.findBuiltInUserStoreAdministrator( Mockito.any( UserStoreKey.class ) ) ).thenReturn( usAdmin );

        final List<UserStoreName> names = Lists.newArrayList();
        UserStoreNames userStores = UserStoreNames.from( "default", "enonic" );
        final UpdateUserStores command =
            Commands.userStore().update().names( UserStoreNames.from( "default", "enonic" ) ).editor( new UserStoreEditor()
            {
                @Override
                public boolean edit( final UserStore userStore )
                    throws Exception
                {
                    names.add( userStore.getName() );
                    return true;
                }
            } );
        this.handler.handle( this.context, command );
        Integer result = command.getResult();
        assertNotNull( result );
        assertEquals( 2l, result.longValue() );
        assertEquals( userStores, UserStoreNames.from( names ) );
    }

    @Override
    public UserDao getUserDao()
    {
        return userDao;
    }

    @Override
    public UserStoreDao getUserStoreDao()
    {
        return userStoreDao;
    }

    @Override
    public GroupDao getGroupDao()
    {
        return groupDao;
    }
}
