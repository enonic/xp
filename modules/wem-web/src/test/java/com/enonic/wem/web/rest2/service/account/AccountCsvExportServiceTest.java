package com.enonic.wem.web.rest2.service.account;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.AccountSearchResults;
import com.enonic.wem.core.search.account.AccountType;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import static org.junit.Assert.*;

public class AccountCsvExportServiceTest
{

    private AccountCsvExportService accountCsvExportService;

    @Before
    public void setUp()
    {
        accountCsvExportService = new AccountCsvExportService();
        UserDao userDao = Mockito.mock( UserDao.class );
        Mockito.when( userDao.findByKey( "856A22BB46C76B4D8A7787C504E227D2F391D5F0" ) ).thenReturn(
            createUserEntity( "Test user 1", "tuser1", "tuser1@enonic.com" ) );
        Mockito.when( userDao.findByKey( "BE9891A338852C102F398CBA65E92626ABD893AC" ) ).thenReturn(
            createUserEntity( "Test user 2", "tuser2", "tuser2@enonic.com" ) );
        GroupDao groupDao = Mockito.mock( GroupDao.class );
        Mockito.when( groupDao.findByKey( new GroupKey( "98D9DCC6E25B94DF499FB233AFD1A2665BE4997C" ) ) ).thenReturn(
            createGroupEntity( "Test group 1" ) );
        Mockito.when( groupDao.findByKey( new GroupKey( "0E91A6F5CCCF8464C39CB4D06AA1715B7750B4E6" ) ) ).thenReturn(
            createGroupEntity( "Test group 2" ) );
        Mockito.when( groupDao.findByKey( new GroupKey( "A2F5AA36DFE832EDCE705507D537D5083A309666" ) ) ).thenReturn(
            createGroupEntity( "Test group 3" ) );
        accountCsvExportService.setUserDao( userDao );
        accountCsvExportService.setGroupDao( groupDao );
    }

    @Test
    public void testGenerateCsv()
    {
        AccountSearchResults results = createSearchResults();
        String csv = accountCsvExportService.generateCsv( results );
        AccountSearchResults emptyResult = new AccountSearchResults( 0, 0 );
        String emptyCsv = accountCsvExportService.generateCsv( emptyResult );
        int end = csv.indexOf( "\n" );
        assertTrue( "Header is generated right", csv.substring( 0, end + 1 ).compareTo( emptyCsv ) == 0 );
        assertSame( "There should be 6 line: 1 for header, and 5 for data", csv.split( "\n" ).length, 6 );
    }

    private AccountSearchResults createSearchResults()
    {
        AccountSearchResults results = new AccountSearchResults( 0, 5 );
        results.add( new AccountKey( "856A22BB46C76B4D8A7787C504E227D2F391D5F0" ), AccountType.USER, 1f );
        results.add( new AccountKey( "BE9891A338852C102F398CBA65E92626ABD893AC" ), AccountType.USER, 1f );
        results.add( new AccountKey( "98D9DCC6E25B94DF499FB233AFD1A2665BE4997C" ), AccountType.GROUP, 1f );
        results.add( new AccountKey( "0E91A6F5CCCF8464C39CB4D06AA1715B7750B4E6" ), AccountType.GROUP, 1f );
        results.add( new AccountKey( "A2F5AA36DFE832EDCE705507D537D5083A309666" ), AccountType.GROUP, 1f );
        return results;
    }

    private UserEntity createUserEntity( String displayName, String name, String email )
    {
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setName( "default" );
        UserEntity user = new UserEntity();
        user.setDisplayName( displayName );
        user.setName( name );
        user.setEmail( email );
        user.setUserStore( userStore );
        user.setTimestamp( DateTime.now() );
        return user;
    }

    private GroupEntity createGroupEntity( String name )
    {
        GroupEntity group = new GroupEntity();
        group.setName( name );
        group.setType( GroupType.DEVELOPERS );
        return group;
    }
}
