package com.enonic.wem.web.rest.resource.account;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.GroupKey;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.RoleKey;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.Gender;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.api.account.query.AccountFacets;
import com.enonic.wem.api.account.query.AccountQueryHits;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.api.command.account.GetAccounts;

import static junit.framework.Assert.assertEquals;

public class AccountExportResourceTest
{

    private Client client;

    private static final String ENCODING_UTF_8 = "UTF-8";

    @Before
    public void setup()
    {
        client = Mockito.mock( Client.class );
    }

    @Test
    public void testAccountExportByKeys()
        throws Exception
    {
        final UserAccount admin = createUser( AccountKey.superUser() );
        final UserAccount anonymous = createUser( AccountKey.anonymous() );
        final UserAccount user1 = createUser( "enonic:user1" );
        user1.setProfile( createUserProfile() );
        final GroupAccount group1 = createGroup( "enonic:group1", user1.getKey() );
        final RoleAccount role1 = createRole( "system:contributors", user1.getKey() );
        final Accounts accounts = createAccountsObject( user1, group1, role1, admin, anonymous );

        Mockito.when( client.execute( Mockito.isA( GetAccounts.class ) ) ).thenReturn( accounts );

        final AccountExportResource accountExportResource = new AccountExportResource();
        accountExportResource.setClient( client );
        final Response response = accountExportResource.byKeys( Lists.newArrayList( "user:enonic:usr" ), ENCODING_UTF_8, "," );
        assertEquals( 200, response.getStatus() );
        final String responseContent = new String( (byte[]) response.getEntity(), ENCODING_UTF_8 );
        assertEqualsCsv( readResource( "accounts_export.csv" ), responseContent.trim() );
    }

    @Test
    public void testAccountExportByQuery()
        throws Exception
    {
        final UserAccount admin = createUser( AccountKey.superUser() );
        final UserAccount anonymous = createUser( AccountKey.anonymous() );
        final UserAccount user1 = createUser( "enonic:user1" );
        user1.setProfile( createUserProfile() );
        final GroupAccount group1 = createGroup( "enonic:group1", user1.getKey() );
        final RoleAccount role1 = createRole( "system:contributors", user1.getKey() );
        final Accounts accounts = createAccountsObject( user1, group1, role1, admin, anonymous );

        Mockito.when( client.execute( Mockito.isA( FindAccounts.class ) ) ).thenReturn( createAccountResult( 10, accounts ) );
        Mockito.when( client.execute( Mockito.isA( GetAccounts.class ) ) ).thenReturn( accounts );

        final AccountExportResource accountExportResource = new AccountExportResource();
        accountExportResource.setClient( client );
        final Response response = accountExportResource.byQuery( "", "user,role,group", "enonic", ENCODING_UTF_8, "," );
        assertEquals( 200, response.getStatus() );
        final String responseContent = new String( (byte[]) response.getEntity(), ENCODING_UTF_8 );
        assertEqualsCsv( readResource( "accounts_export.csv" ), responseContent.trim() );
    }

    private void assertEqualsCsv( final String expectedContent, final String actualContent )
    {
        assertEquals( normalizeLineBreakChars( expectedContent.trim() ), normalizeLineBreakChars( actualContent.trim() ) );
    }

    private String normalizeLineBreakChars( final String value )
    {
        return value.replace( "\r\n", "\n" ).replace( "\r", "\n" ).replace( "\n", "\r\n" );
    }

    private AccountQueryHits createAccountResult( final int totalSize, final Accounts accounts )
    {
        final AccountQueryHits result = new AccountQueryHits( totalSize, accounts.getKeys() );
        result.setFacets( new AccountFacets() );
        return result;
    }

    private UserProfile createUserProfile()
    {
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
        return profile;
    }

    private Accounts createAccountsObject( final Account... accounts )
    {
        return Accounts.from( accounts );
    }

    private UserAccount createUser( final String qName )
    {
        final AccountKey accountKey = UserKey.from( qName );
        return createUser( accountKey );
    }

    private UserAccount createUser( final AccountKey accountKey )
    {
        final UserAccount user = UserAccount.create( accountKey.asUser() );
        user.setDisplayName( accountKey.getLocalName().toUpperCase() );
        user.setEmail( accountKey.getLocalName() + "@" + accountKey.getUserStore() + ".com" );
        user.setCreatedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        user.setModifiedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        user.setImage( "image".getBytes() );
        return user;
    }

    private GroupAccount createGroup( final String qName, final AccountKey... members )
    {
        final AccountKey accountKey = GroupKey.from( qName );
        final GroupAccount group = GroupAccount.create( accountKey.asGroup() );
        group.setDisplayName( accountKey.getLocalName().toUpperCase() );
        group.setCreatedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setModifiedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setMembers( AccountKeys.from( members ) );
        return group;
    }

    private RoleAccount createRole( final String qName, final AccountKey... members )
    {
        final AccountKey accountKey = RoleKey.from( qName );
        final RoleAccount group = RoleAccount.create( accountKey.asRole() );
        group.setDisplayName( accountKey.getLocalName().toUpperCase() );
        group.setCreatedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setModifiedTime( DateTime.parse( "2012-01-01T10:01:10.101+01:00" ) );
        group.setMembers( AccountKeys.from( members ) );
        return group;
    }

    private String readResource( final String fileName )
        throws IOException
    {
        return IOUtils.toString( getClass().getResourceAsStream( fileName ) );
    }
}
