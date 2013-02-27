package com.enonic.wem.core.account;

import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.GroupKey;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.RoleKey;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.account.editor.AccountEditor;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.Gender;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.UpdateAccounts;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.index.IndexService;

import static org.junit.Assert.*;

public class UpdateAccountsHandlerTest
    extends AbstractCommandHandlerTest
{
    private AccountDao accountDao;

    private UpdateAccountsHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );
        final IndexService indexService = Mockito.mock( IndexService.class );

        handler = new UpdateAccountsHandler();
        handler.setAccountDao( accountDao );
        handler.setIndexService( indexService );
    }

    @Test
    public void testUpdateNoModifications()
        throws Exception
    {
        // setup
        createGroup( "enonic", "group1" );
        createRole( "enonic", "contributors" );
        createUser( "enonic", "user1" );

        // exercise
        final AccountKeys accounts = AccountKeys.from( "group:enonic:group1", "role:enonic:contributors", "user:enonic:user1" );

        final Set<AccountKey> keysEdited = Sets.newHashSet();
        final UpdateAccounts command = Commands.account().update().keys( accounts ).editor( new AccountEditor()
        {
            @Override
            public boolean edit( final Account account )
                throws Exception
            {
                keysEdited.add( account.getKey() );
                return false;
            }
        } );
        this.handler.handle( this.context, command );
        final Integer totalUpdated = command.getResult();

        // verify
        assertNotNull( totalUpdated );
        assertEquals( 0l, totalUpdated.longValue() );
        assertEquals( accounts.getSet(), keysEdited );
    }

    @Test
    public void testUpdateGroupsWithMembers()
        throws Exception
    {
        // setup
        final GroupAccount group1 = createGroup( "enonic", "group1" );
        final RoleAccount role1 = createRole( "enonic", "contributors" );
        final UserAccount existingMember1 = createUser( "enonic", "member1" );
        final GroupAccount existingMember2 = createGroup( "enonic", "groupA" );

        group1.setMembers( AccountKeys.from( existingMember1.getKey() ) );
        role1.setMembers( AccountKeys.from( existingMember1.getKey(), existingMember2.getKey() ) );

        createUser( "enonic", "user1" );
        createUser( "enonic", "user2" );
        createUser( "enonic", "user3" );

        // exercise
        final AccountKeys accounts = AccountKeys.from( "group:enonic:group1", "role:enonic:contributors" );

        final Set<AccountKey> keysEdited = Sets.newHashSet();
        final UpdateAccounts command = Commands.account().update().keys( accounts ).editor( new AccountEditor()
        {
            @Override
            public boolean edit( final Account account )
                throws Exception
            {
                account.setDisplayName( account.getDisplayName() + "_updated" );
                if ( account.getKey().isGroup() )
                {
                    ( (GroupAccount) account ).setMembers(
                        AccountKeys.from( "user:enonic:user1", "user:enonic:user2", "group:enonic:groupA" ) );
                }
                else if ( account.getKey().isRole() )
                {
                    ( (RoleAccount) account ).setMembers( AccountKeys.from( "user:enonic:user3" ) );
                }

                keysEdited.add( account.getKey() );
                return true;
            }
        } );
        this.handler.handle( this.context, command );
        final Integer totalUpdated = command.getResult();

        // verify
        assertNotNull( totalUpdated );
        assertEquals( 2l, totalUpdated.longValue() );
        assertEquals( accounts.getSet(), keysEdited );
    }

    @Test
    public void testUpdateUsers()
        throws Exception
    {
        // setup
        createUser( "enonic", "user1" );
        createUser( "enonic", "user2" );
        createUser( "enonic", "user3" );

        // exercise
        final AccountKeys accounts = AccountKeys.from( "user:enonic:user1", "user:enonic:user2", "user:enonic:user3" );

        final Set<AccountKey> keysEdited = Sets.newHashSet();
        final UpdateAccounts command = Commands.account().update().keys( accounts ).editor( new AccountEditor()
        {
            @Override
            public boolean edit( final Account account )
                throws Exception
            {
                account.setDisplayName( account.getDisplayName() + "_updated" );
                final UserAccount user = (UserAccount) account;
                user.setEmail( user.getKey().getLocalName() + "enonic.com" );
                user.setImage( "photo_data".getBytes() );
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

                keysEdited.add( account.getKey() );
                return true;
            }
        } );
        this.handler.handle( this.context, command );
        final Integer totalUpdated = command.getResult();

        // verify
        assertNotNull( totalUpdated );
        assertEquals( 3l, totalUpdated.longValue() );
        assertEquals( accounts.getSet(), keysEdited );
    }

    @Test
    public void testUpdateMissingAccounts()
        throws Exception
    {
        // setup
        final AccountKeys accounts = AccountKeys.from( "group:enonic:group1", "role:enonic:contributors", "user:enonic:user1" );
        final Set<AccountKey> keysEdited = Sets.newHashSet();

        // exercise
        final UpdateAccounts command = Commands.account().update().keys( accounts ).editor( new AccountEditor()
        {
            @Override
            public boolean edit( final Account account )
                throws Exception
            {
                account.setDisplayName( account.getDisplayName() + "_updated" );
                keysEdited.add( account.getKey() );
                return true;
            }
        } );
        this.handler.handle( this.context, command );
        final Integer totalUpdated = command.getResult();

        // verify
        assertNotNull( totalUpdated );
        assertEquals( 0l, totalUpdated.longValue() );
        assertTrue( keysEdited.isEmpty() );
    }

    private RoleAccount createRole( final String userStore, final String name )
        throws Exception
    {
        final RoleAccount role = RoleAccount.create( userStore + ":" + name );
        role.setDisplayName( "Role " + name );
        role.setDeleted( false );
        role.setMembers( AccountKeys.empty() );

        final RoleKey accountKey = role.getKey().asRole();
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

        final GroupKey accountKey = group.getKey().asGroup();
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
        final UserKey accountKey = user.getKey().asUser();
        Mockito.when( accountDao.findUser( Matchers.eq( accountKey ), Matchers.anyBoolean(), Matchers.anyBoolean(),
                                           Matchers.eq( session ) ) ).thenReturn( user );
        return user;
    }
}
