package com.enonic.wem.core.account;

import java.util.Locale;
import java.util.TimeZone;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.Gender;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.CreateAccount;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.search.account.AccountSearchService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class CreateAccountHandlerTest
    extends AbstractCommandHandlerTest
{
    private AccountDao accountDao;

    private CreateAccountHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );
        final AccountSearchService accountSearchService = Mockito.mock( AccountSearchService.class );

        handler = new CreateAccountHandler();
        handler.setAccountDao( accountDao );
        handler.setSearchService( accountSearchService );
    }

    @Test
    public void testCreateUser()
        throws Exception
    {
        final UserAccount user = UserAccount.create( "enonic:user1" );
        user.setEmail( "user1@enonic.com" );
        user.setDisplayName( "The User #1" );
        user.setImage( "photodata".getBytes() );
        final UserProfile profile = new UserProfile();
        profile.setFax( "fax" );
        profile.setBirthday( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        profile.setCountry( "country" );
        profile.setDescription( "description" );
        profile.setFirstName( "first-name" );
        profile.setGender( Gender.FEMALE );
        profile.setGlobalPosition( "global-position" );
        profile.setHomePage( "home-page" );
        profile.setHtmlEmail( true );
        profile.setInitials( "initials" );
        profile.setLastName( "last-name" );
        profile.setLocale( Locale.ENGLISH );
        profile.setMemberId( "member-id" );
        profile.setMiddleName( "middle-name" );
        profile.setMobile( "mobile" );
        profile.setNickName( "nick-name" );
        profile.setOrganization( "organization" );
        profile.setPersonalId( "personal-id" );
        profile.setPhone( "phone" );
        profile.setPrefix( "prefix" );
        profile.setSuffix( "suffix" );
        profile.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        profile.setTitle( "title" );
        final Address address = new Address();
        address.setLabel( "label" );
        address.setCountry( "country" );
        address.setIsoCountry( "iso-country" );
        address.setRegion( "region" );
        address.setIsoRegion( "iso-region" );
        address.setPostalAddress( "postal-address" );
        address.setPostalCode( "postal-code" );
        address.setStreet( "street" );
        profile.setAddresses( Addresses.from( address ) );
        user.setProfile( profile );

        // exercise
        CreateAccount command = Commands.account().create().account( user );
        this.handler.handle( this.context, command );
        final AccountKey createdUserKey = command.getResult();

        // verify
        verify( accountDao, atLeastOnce() ).createUser( Mockito.any( Session.class ), Mockito.eq( user ) );
        assertNotNull( createdUserKey );
        assertTrue( createdUserKey.isUser() );
        assertEquals( user.getKey(), createdUserKey );
    }

    @Test
    public void testCreateGroup()
        throws Exception
    {
        // setup
        final GroupAccount group = GroupAccount.create( "enonic:group1" );
        group.setDisplayName( "The group #1" );

        // exercise
        CreateAccount command = Commands.account().create().account( group );
        this.handler.handle( this.context, command );
        final AccountKey createdGroupKey = command.getResult();

        // verify
        verify( accountDao, atLeastOnce() ).createGroup( Mockito.any( Session.class ), Mockito.eq( group ) );
        assertNotNull( createdGroupKey );
        assertTrue( createdGroupKey.isGroup() );
        assertEquals( group.getKey(), createdGroupKey );
    }

    @Test
    public void testCreateRole()
        throws Exception
    {
        // setup
        final RoleAccount role = RoleAccount.create( "enonic:role1" );
        role.setDisplayName( "The role #1" );

        // exercise
        CreateAccount command = Commands.account().create().account( role );
        this.handler.handle( this.context, command );
        final AccountKey createdRoleKey = command.getResult();

            // verify
        verify( accountDao, atLeastOnce() ).createRole( Mockito.any( Session.class ), Mockito.eq( role ) );
        assertNotNull( createdRoleKey );
        assertTrue( createdRoleKey.isRole() );
        assertEquals( role.getKey(), createdRoleKey );
    }
}
