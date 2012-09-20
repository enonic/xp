package com.enonic.wem.web.rest.rpc.account;

import java.util.List;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.Address;
import com.enonic.wem.api.account.profile.Addresses;
import com.enonic.wem.api.account.profile.UserProfile;

class GetAccountJsonResult
    extends AbstractAccountJsonResult
{
    protected Account account;

    protected List<Account> members;

    protected List<Account> memberships;

    public GetAccountJsonResult( Account account, List<Account> members, List<Account> memberships )
    {
        super();
        this.account = account;
        this.members = members;
        this.memberships = memberships;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        serializeAccount( json, account );
        if ( account instanceof UserAccount )
        {
            final UserProfile profile = ( (UserAccount) account ).getProfile();
            if ( profile != null )
            {
                json.put( "profile", serializeUserProfile( profile ) );
            }

            json.put( "memberships", serialize( memberships ) );
        }
        else
        {
            json.put( "members", serialize( members ) );
        }
    }

    private ObjectNode serializeUserProfile( final UserProfile profile )
    {
        final ObjectNode json = objectNode();
        json.put( "firstName", profile.getFirstName() );
        json.put( "lastName", profile.getLastName() );
        json.put( "middleName", profile.getMiddleName() );
        if ( profile.getBirthday() != null )
        {
            json.put( "birthday", profile.getBirthday().toString() );
        }
        json.put( "country", profile.getCountry() );
        json.put( "description", profile.getDescription() );
        json.put( "initials", profile.getInitials() );
        json.put( "globalPosition", profile.getGlobalPosition() );
        json.put( "htmlEmail", profile.getHtmlEmail() );
        if ( profile.getLocale() != null )
        {
            json.put( "locale", profile.getLocale().toString() );
        }
        json.put( "nickName", profile.getNickName() );
        json.put( "personalId", profile.getPersonalId() );
        json.put( "memberId", profile.getMemberId() );
        json.put( "organization", profile.getOrganization() );
        json.put( "prefix", profile.getPrefix() );
        json.put( "suffix", profile.getSuffix() );
        json.put( "title", profile.getTitle() );
        json.put( "homePage", profile.getHomePage() );
        json.put( "mobile", profile.getMobile() );
        json.put( "phone", profile.getPhone() );
        json.put( "fax", profile.getFax() );
        if ( profile.getGender() != null )
        {
            json.put( "gender", profile.getGender().name() );
        }
        if ( profile.getTimeZone() != null )
        {
            json.put( "timezone", profile.getTimeZone().getID() );
        }
        if ( profile.getAddresses() != null )
        {
            json.put( "addresses", serializeAddresses( profile.getAddresses() ) );
        }
        return json;
    }

    private ArrayNode serializeAddresses( final Addresses addresses )
    {
        final ArrayNode json = arrayNode();
        for ( Address address : addresses )
        {
            final ObjectNode jsonAddress = objectNode();
            jsonAddress.put( "country", address.getCountry() );
            jsonAddress.put( "isoCountry", address.getIsoCountry() );
            jsonAddress.put( "region", address.getRegion() );
            jsonAddress.put( "isoRegion", address.getIsoRegion() );
            jsonAddress.put( "label", address.getLabel() );
            jsonAddress.put( "street", address.getStreet() );
            jsonAddress.put( "postalCode", address.getPostalCode() );
            jsonAddress.put( "postalAddress", address.getPostalAddress() );
            json.add( jsonAddress );
        }
        return json;
    }

    private ArrayNode serialize( final List<Account> accounts )
    {
        ArrayNode jsons = arrayNode();
        if ( accounts != null )
        {
            for ( Account account : accounts )
            {
                serializeAccount( jsons.addObject(), account );
            }
        }
        return jsons;
    }
}
