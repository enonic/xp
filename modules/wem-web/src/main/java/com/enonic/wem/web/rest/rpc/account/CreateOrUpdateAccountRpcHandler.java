package com.enonic.wem.web.rest.rpc.account;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.editor.AccountEditor;
import com.enonic.wem.api.account.editor.AccountEditors;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.Gender;
import com.enonic.wem.api.account.profile.UserProfile;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.service.upload.UploadItem;
import com.enonic.wem.web.rest.service.upload.UploadService;

@Component
public final class CreateOrUpdateAccountRpcHandler
    extends AbstractDataRpcHandler
{
    private UploadService uploadService;

    public CreateOrUpdateAccountRpcHandler()
    {
        super( "account_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final Account account = getAccountFromRequest( context );
        if ( accountExists( account.getKey() ) )
        {
            updateAccount( account );
            context.setResult( CreateOrUpdateAccountJsonResult.updated() );
        }
        else
        {
            createAccount( account );
            context.setResult( CreateOrUpdateAccountJsonResult.created() );
        }

        if ( account.getKey().isUser() && !context.param( "groups" ).isNull() )
        {
            setUserMemberships( account, context );
        }
    }

    private void setUserMemberships( final Account user, final JsonRpcContext context )
    {
        // get current memberships for the user
        final AccountKeys currentMemberships = this.client.execute( Commands.account().findMemberships().key( user.getKey() ) );
        final String[] groupKeys = context.param( "groups" ).asStringArray();
        final AccountKeys newMemberships = AccountKeys.from( groupKeys );
        final AccountKeys userMember = AccountKeys.from( user.getKey() );

        // remove user from groups that are not specified in the request
        for ( final AccountKey groupKey : currentMemberships )
        {
            if ( !newMemberships.contains( groupKey ) )
            {
                final AccountKeys groupToRemove = AccountKeys.from( groupKey );
                this.client.execute(
                    Commands.account().update().keys( groupToRemove ).editor( AccountEditors.removeMembers( userMember ) ) );
            }
        }

        // add user as member of specified groups
        for ( final AccountKey groupKey : newMemberships )
        {
            final AccountKeys groupToAdd = AccountKeys.from( groupKey );
            this.client.execute( Commands.account().update().keys( groupToAdd ).editor( AccountEditors.addMembers( userMember ) ) );
        }
    }

    private boolean accountExists( final AccountKey accountKey )
    {
        final Accounts accounts = this.client.execute( Commands.account().get().keys( AccountKeys.from( accountKey ) ) );
        return !accounts.isEmpty();
    }

    private void createAccount( final Account account )
        throws Exception
    {
        this.client.execute( Commands.account().create().account( account ) );
    }

    private void updateAccount( final Account account )
        throws Exception
    {
        final AccountKeys fromKey = AccountKeys.from( account.getKey() );
        final AccountEditor setAccount = AccountEditors.setAccount( account );
        this.client.execute( Commands.account().update().keys( fromKey ).editor( setAccount ) );
    }

    private Account getAccountFromRequest( final JsonRpcContext context )
        throws Exception
    {
        final String key = context.param( "key" ).required().asString();
        final AccountKey accountKey = AccountKey.from( key );

        final Account account;
        if ( accountKey.isUser() )
        {
            final UserAccount user = UserAccount.create( accountKey );
            user.setEmail( context.param( "email" ).required().asString() );
            if ( !context.param( "imageRef" ).isNull() )
            {
                final byte[] image = getImageContent( context.param( "imageRef" ).asString() );
                user.setImage( image );
            }

            if ( !context.param( "profile" ).isNull() )
            {
                user.setProfile( getProfileFromRequest( context ) );
            }
            account = user;
        }
        else
        {
            final NonUserAccount nonUserAccount;
            if ( accountKey.isGroup() )
            {
                nonUserAccount = GroupAccount.create( accountKey );
            }
            else
            {
                nonUserAccount = RoleAccount.create( accountKey );
            }
            final String[] members = context.param( "members" ).asStringArray();
            nonUserAccount.setMembers( AccountKeys.from( members ) );
            account = nonUserAccount;
        }
        account.setDisplayName( context.param( "displayName" ).required().asString() );
        return account;
    }

    private UserProfile getProfileFromRequest( final JsonRpcContext context )
    {
        final UserProfile profile = new UserProfile();
        final ObjectNode profileJson = context.param( "profile" ).asObject();

        profile.setCountry( jsonFieldAsString( profileJson, "country" ) );
        profile.setFax( jsonFieldAsString( profileJson, "fax" ) );
        profile.setDescription( jsonFieldAsString( profileJson, "description" ) );
        profile.setFirstName( jsonFieldAsString( profileJson, "firstName" ) );
        profile.setGlobalPosition( jsonFieldAsString( profileJson, "globalPosition" ) );
        profile.setHomePage( jsonFieldAsString( profileJson, "homePage" ) );
        profile.setInitials( jsonFieldAsString( profileJson, "initials" ) );
        profile.setLastName( jsonFieldAsString( profileJson, "lastName" ) );
        profile.setMemberId( jsonFieldAsString( profileJson, "memberId" ) );
        profile.setMiddleName( jsonFieldAsString( profileJson, "middleName" ) );
        profile.setMobile( jsonFieldAsString( profileJson, "mobile" ) );
        profile.setNickName( jsonFieldAsString( profileJson, "nickName" ) );
        profile.setOrganization( jsonFieldAsString( profileJson, "organization" ) );
        profile.setPersonalId( jsonFieldAsString( profileJson, "personalId" ) );
        profile.setPhone( jsonFieldAsString( profileJson, "phone" ) );
        profile.setPrefix( jsonFieldAsString( profileJson, "prefix" ) );
        profile.setSuffix( jsonFieldAsString( profileJson, "suffix" ) );
        profile.setTitle( jsonFieldAsString( profileJson, "title" ) );

        profile.setBirthday( jsonFieldAsDate( profileJson, "birthday" ) );
        if ( profileJson.has( "gender" ) )
        {
            profile.setGender( Gender.valueOf( jsonFieldAsString( profileJson, "gender" ).toUpperCase() ) );
        }
        if ( profileJson.has( "htmlEmail" ) )
        {
            profile.setHtmlEmail( true );
        }
        if ( profileJson.has( "locale" ) )
        {
            profile.setLocale( new Locale( jsonFieldAsString( profileJson, "locale" ) ) );
        }
        if ( profileJson.has( "timezone" ) )
        {
            profile.setTimeZone( TimeZone.getTimeZone( jsonFieldAsString( profileJson, "timezone" ) ) );
        }

        if ( profileJson.has( "addresses" ) )
        {
            final Iterator<JsonNode> addressesJson = profileJson.get( "addresses" ).getElements();
            final List<Address> addressList = Lists.newArrayList();
            while ( addressesJson.hasNext() )
            {
                final ObjectNode addressJson = (ObjectNode) addressesJson.next();
                final Address address = new Address();
                address.setLabel( jsonFieldAsString( addressJson, "label" ) );
                address.setCountry( jsonFieldAsString( addressJson, "country" ) );
                address.setIsoCountry( jsonFieldAsString( addressJson, "isoCountry" ) );
                address.setRegion( jsonFieldAsString( addressJson, "region" ) );
                address.setIsoRegion( jsonFieldAsString( addressJson, "isoRegion" ) );
                address.setPostalAddress( jsonFieldAsString( addressJson, "postalAddress" ) );
                address.setPostalCode( jsonFieldAsString( addressJson, "postalCode" ) );
                address.setStreet( jsonFieldAsString( addressJson, "street" ) );
                addressList.add( address );
            }
            profile.setAddresses( Addresses.from( addressList ) );
        }
        return profile;
    }

    private String jsonFieldAsString( final ObjectNode jsonObject, final String fieldName )
    {
        return jsonObject.has( fieldName ) ? jsonObject.get( fieldName ).asText() : null;
    }

    private DateTime jsonFieldAsDate( final ObjectNode jsonObject, final String fieldName )
    {
        if ( !jsonObject.has( fieldName ) )
        {
            return null;
        }
        final String value = jsonObject.get( fieldName ).asText();
        try
        {
            return value == null ? null : DateTime.parse( value );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
    }

    private byte[] getImageContent( final String imageId )
        throws IOException
    {
        final UploadItem item = uploadService.getItem( imageId );
        if ( item != null )
        {
            final File file = item.getFile();
            if ( file.exists() )
            {
                return FileUtils.readFileToByteArray( file );
            }
        }
        return null;
    }

    @Autowired
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}