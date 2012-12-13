package com.enonic.wem.core.account;

import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.Gender;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.GetAccounts;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class GetAccountsHandlerTest
    extends AbstractCommandHandlerTest
{
    private AccountDao accountDao;

    private GetAccountsHandler handler;


    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );

        handler = new GetAccountsHandler();
        handler.setAccountDao( accountDao );
    }

    @Test
    public void testGetAccounts()
        throws Exception
    {
        // setup
        createGroup( "enonic", "group1" );
        createRole( "enonic", "contributors" );
        createUser( "enonic", "user1" );

        // exercise
        final AccountKeys accounts = AccountKeys.from( "group:enonic:group1", "role:enonic:contributors", "user:enonic:user1" );

        final GetAccounts command = Commands.account().get().keys( accounts ).includeImage();
        this.handler.handle( this.context, command );
        Accounts accountResult = command.getResult();

        // verify
        assertNotNull( accountResult );
        assertEquals( 3, accountResult.getSize() );
        assertEquals( "group:enonic:group1", accountResult.first().getKey().toString() );
        assertEquals( "role:enonic:contributors", accountResult.getList().get( 1 ).getKey().toString() );
        assertEquals( "user:enonic:user1", accountResult.getList().get( 2 ).getKey().toString() );
    }

    @Test
    public void testGetAccountsWithMembers()
        throws Exception
    {
        // setup
        final UserAccount user1 = createUser( "enonic", "user1" );
        final UserAccount user2 = createUser( "enonic", "user2" );
        final UserAccount user3 = createUser( "enonic", "user3" );
        final GroupAccount group1 = createGroup( "enonic", "group1" );
        final GroupAccount group2 = createGroup( "enonic", "group2" );
        final GroupAccount group3 = createGroup( "enonic", "group3" );
        final RoleAccount role1 = createRole( "enonic", "contributors" );
        final RoleAccount role2 = createRole( "enonic", "administrators" );
        group1.setMembers( AccountKeys.from( user1.getKey(), user2.getKey() ) );
        group2.setMembers( AccountKeys.from( user3.getKey() ) );
        role1.setMembers( AccountKeys.from( user3.getKey(), group1.getKey(), role2.getKey() ) );

        // exercise
        final AccountKeys accounts = AccountKeys.from( "group:enonic:group1", "group:enonic:group2", "role:enonic:contributors" );

        final GetAccounts command = Commands.account().get().keys( accounts ).includeMembers().includeImage();
        this.handler.handle( this.context, command );
        Accounts accountResult = command.getResult();

        // verify
        assertNotNull( accountResult );
        assertEquals( 3, accountResult.getSize() );

        assertTrue( accountResult.first() instanceof GroupAccount );
        assertTrue( accountResult.getList().get( 1 ) instanceof GroupAccount );
        assertTrue( accountResult.getList().get( 2 ) instanceof RoleAccount );
        final GroupAccount group1Account = (GroupAccount) accountResult.first();
        final GroupAccount group2Account = (GroupAccount) accountResult.getList().get( 1 );
        final RoleAccount roleAccount = (RoleAccount) accountResult.getList().get( 2 );

        assertEquals( "group:enonic:group1", group1Account.getKey().toString() );
        assertEquals( "group:enonic:group2", group2Account.getKey().toString() );
        assertEquals( "role:enonic:contributors", roleAccount.getKey().toString() );

        assertTrue( group1Account.getMembers().contains( AccountKey.user( "enonic:user1" ) ) );
        assertTrue( group1Account.getMembers().contains( AccountKey.user( "enonic:user2" ) ) );

        assertTrue( group2Account.getMembers().contains( AccountKey.user( "enonic:user3" ) ) );
        assertTrue( roleAccount.getMembers().contains( AccountKey.user( "enonic:user3" ) ) );
        assertTrue( roleAccount.getMembers().contains( AccountKey.group( "enonic:group1" ) ) );
        assertTrue( roleAccount.getMembers().contains( AccountKey.role( "enonic:administrators" ) ) );
    }

    @Test
    public void testGetUserAccountWithProfile()
        throws Exception
    {
        // setup
        final UserAccount user = createUser( "enonic", "user1" );
        user.setImage( "img".getBytes() );
        final UserProfile profile = new UserProfile();
        profile.setBirthday( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        profile.setCountry( "Norway" );
        profile.setDescription( "Description" );
        profile.setFax( "12345678" );
        profile.setFirstName( "John" );
        profile.setGender( Gender.MALE );
        profile.setGlobalPosition( "71 Degrees North" );
        profile.setHomePage( "http://acme.org" );
        profile.setHtmlEmail( true );
        profile.setInitials( "JS." );
        profile.setLastName( "Smith" );
        profile.setLocale( new Locale( "NO" ) );
        profile.setMemberId( "1234509876" );
        profile.setMiddleName( "William" );
        profile.setMobile( "99999999" );
        profile.setNickName( "JS" );
        profile.setOrganization( "ACME" );
        profile.setPersonalId( "1234509876A" );
        profile.setPhone( "66666666" );
        profile.setPrefix( "Pre" );
        profile.setSuffix( "Suff" );
        profile.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        profile.setTitle( "Mr." );
        Address address = new Address();
        address.setCountry( "Norway" );
        address.setIsoCountry( "NO" );
        address.setRegion( "AK" );
        address.setIsoRegion( "03" );
        address.setLabel( "Home" );
        address.setStreet( "Street" );
        address.setPostalCode( "1234" );
        address.setPostalAddress( "1" );
        profile.setAddresses( Addresses.from( address ) );
        user.setProfile( profile );

        // exercise
        final AccountKeys accounts = AccountKeys.from( "user:enonic:user1" );

        final GetAccounts command = Commands.account().get().keys( accounts ).includeProfile();
        this.handler.handle( this.context, command );
        Accounts accountResult = command.getResult();

        // verify
        assertNotNull( accountResult );
        assertEquals( 1, accountResult.getSize() );
        assertEquals( "user:enonic:user1", accountResult.first().getKey().toString() );
        final UserProfile prof = ( (UserAccount) accountResult.first() ).getProfile();
        assertNotNull( prof );
        assertEquals( "John", prof.getFirstName() );
        assertEquals( "Smith", prof.getLastName() );
        assertNotNull( prof.getAddresses().getPrimary() );
        assertEquals( "Home", prof.getAddresses().getPrimary().getLabel() );
    }

    private RoleAccount createRole( final String userStore, final String name )
        throws Exception
    {
        final RoleAccount role = RoleAccount.create( userStore + ":" + name );
        role.setDisplayName( "Role " + name );
        role.setDeleted( false );
        role.setMembers( AccountKeys.empty() );

        final AccountKey accountKey = role.getKey();
        Mockito.when( accountDao.findRole( Matchers.eq( accountKey ), Matchers.anyBoolean(), Matchers.eq( session ) ) ).thenReturn( role );
        return role;
    }

    private GroupAccount createGroup( final String userStore, final String name )
        throws Exception
    {
        final GroupAccount group = GroupAccount.create( userStore + ":" + name );
        group.setDisplayName( "Group " + name );
        group.setDeleted( false );
        group.setMembers( AccountKeys.empty() );

        final AccountKey accountKey = group.getKey();
        Mockito.when( accountDao.findGroup( Matchers.eq( accountKey ), Matchers.anyBoolean(), Matchers.eq( session ) ) ).thenReturn(
            group );
        return group;
    }

    private UserAccount createUser( final String userStore, final String name )
        throws Exception
    {
        final UserAccount user = UserAccount.create( userStore + ":" + name );
        user.setEmail( "user@email.com" );
        user.setDisplayName( "User " + name );
        user.setDeleted( false );
        final AccountKey accountKey = user.getKey();
        Mockito.when( accountDao.findUser( Matchers.eq( accountKey ), Matchers.anyBoolean(), Matchers.anyBoolean(),
                                           Matchers.eq( session ) ) ).thenReturn( user );
        return user;
    }

}
