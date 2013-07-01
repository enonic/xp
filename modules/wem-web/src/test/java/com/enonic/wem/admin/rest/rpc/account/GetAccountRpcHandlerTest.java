package com.enonic.wem.admin.rest.rpc.account;

import java.util.Locale;
import java.util.TimeZone;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.Gender;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.api.command.account.FindMembers;
import com.enonic.wem.api.command.account.FindMemberships;
import com.enonic.wem.api.command.account.GetAccounts;

public class GetAccountRpcHandlerTest
    extends AbstractAccountRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final GetAccountRpcHandler handler = new GetAccountRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void testGetAccountIncorrectKey()
        throws Exception
    {
        testSuccess( createParams( "12345" ), createResult( false, "Not a valid account key [12345]" ) );
    }

    @Test
    public void testGetAccountNoResults()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetAccounts.class ) ) ).thenReturn( createAccountsObject() );
        testSuccess( createParams( "user:enonic:1" ), createResult( false, "No account(s) were found for key [user:enonic:1]" ) );
    }

    @Test
    public void testGetAccountRole()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetAccounts.class ) ) ).thenReturn(
            createAccountsObject( createRole( "enonic:1" ) ) ).thenReturn( createAccountsObject( createUser( "enonic:2" ) ) );

        Mockito.when( client.execute( Mockito.isA( FindMembers.class ) ) ).thenReturn( createAccountKeySet( "user:enonic:2" ) );

        testSuccess( createParams( "role:enonic:1" ), "getAccount_role.json" );
    }

    @Test
    public void testGetAccountGroup()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetAccounts.class ) ) ).thenReturn(
            createAccountsObject( createGroup( "enonic:1" ) ) ).thenReturn( createAccountsObject( createUser( "enonic:2" ) ) );

        Mockito.when( client.execute( Mockito.isA( FindMembers.class ) ) ).thenReturn( createAccountKeySet( "user:enonic:2" ) );

        testSuccess( createParams( "group:enonic:1" ), "getAccount_group.json" );
    }

    @Test
    public void testGetAccountUser()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetAccounts.class ) ) ).thenReturn(
            createAccountsObject( createUser( "enonic:1" ) ) ).thenReturn(
            createAccountsObject( createGroup( "enonic:2" ), createRole( "enonic:3" ) ) );

        Mockito.when( client.execute( Mockito.isA( FindMemberships.class ) ) ).thenReturn( createAccountKeySet( "user:enonic:1" ) );

        testSuccess( createParams( "user:enonic:1" ), "getAccount_user.json" );
    }

    @Test
    public void testGetAccountUserWithProfile()
        throws Exception
    {
        final UserAccount user1 = createUser( "enonic:1" );
        final UserProfile profile = new UserProfile();
        profile.setFax( "fax" );
        profile.setBirthday( new DateTime( 2012, 1, 1, 10, 01, 10, 101, DateTimeZone.UTC ) );
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
        user1.setProfile( profile );
        Mockito.when( client.execute( Mockito.isA( GetAccounts.class ) ) ).thenReturn( createAccountsObject( user1 ) ).thenReturn(
            createAccountsObject( createGroup( "enonic:2" ) ) );

        Mockito.when( client.execute( Mockito.isA( FindMemberships.class ) ) ).thenReturn( createAccountKeySet( "user:enonic:2" ) );

        testSuccess( createParams( "user:enonic:1" ), "getAccount_user_profile.json" );
    }

    private JsonNode createResult( boolean success, String error )
    {
        ObjectNode result = objectNode();
        result.put( "success", success );

        if ( error != null )
        {
            result.put( "error", error );
        }
        return result;
    }

    private JsonNode createParams( final String key )
    {
        ObjectNode params = objectNode();
        params.put( "key", key );
        return params;
    }

    private AccountKeys createAccountKeySet( String... keys )
    {
        return AccountKeys.from( keys );
    }

}
