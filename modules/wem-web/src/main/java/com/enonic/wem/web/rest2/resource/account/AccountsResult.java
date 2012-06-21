package com.enonic.wem.web.rest2.resource.account;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.enonic.wem.web.rest.account.AccountModel;
import com.enonic.wem.web.rest.account.AccountsModel;
import com.enonic.wem.web.rest.account.AddressModel;
import com.enonic.wem.web.rest.account.GroupModel;
import com.enonic.wem.web.rest.account.UserInfoModel;
import com.enonic.wem.web.rest.account.UserModel;
import com.enonic.wem.web.rest2.common.JsonResult;

public final class AccountsResult
        extends JsonResult
{
    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss" );

    private final AccountsModel results;

    public AccountsResult( AccountsModel accountsModel )
    {
        this.results = accountsModel;
    }

    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();
        json.put( "success", true );
        json.put( "results", toJson( this.results ) );
        return json;
    }

    private ObjectNode toJson( final AccountsModel results )
    {
        final ObjectNode json = objectNode();
        json.put( "total", results.getTotal() );

        final ArrayNode accountsJson = arrayNode();
        for ( AccountModel account : results.getAccounts() )
        {
            final ObjectNode resultJson = toJson( account );
            accountsJson.add( resultJson );
        }
        json.put( "accounts", accountsJson );

        return json;
    }

    private ObjectNode toJson( AccountModel account )
    {
        final ObjectNode json = objectNode();
        json.put( "type", account.getAccountType() );
        json.put( "key", account.getKey() );
        json.put( "name", account.getName() );
        json.put( "qualifiedName", account.getQualifiedName() );
        json.put( "userStore", account.getUserStore() );
        json.put( "displayName", account.getDisplayName() );
        putJsonDate( json, "lastModified", account.getLastModified() );
        json.put( "hasPhoto", account.hasPhoto() );
        json.put( "isEditable", account.isEditable() );
        json.put( "builtIn", account.isBuiltIn() );

        if ( account instanceof GroupModel )
        {
            groupToJson( json, (GroupModel) account );
        }
        else if ( account instanceof UserModel )
        {
            userToJson( json, (UserModel) account );
        }

        return json;
    }

    private ObjectNode groupToJson( final ObjectNode json, final GroupModel group )
    {
        json.put( "description", group.getDescription() );
        json.put( "lastLogged", group.getLastLogged() );
        json.put( "membersCount", group.getMembersCount() );
        json.put( "restricted", group.isRestricted() );
        json.put( "public", group.isPublic() );
        final List<AccountModel> members = group.getMembers();
        final ArrayNode membersJson = arrayNode();
        for ( AccountModel member : members )
        {
            final ObjectNode memberJson = toJson( member );
            membersJson.add( memberJson );
        }
        json.put( "members", membersJson );

        return json;
    }

    private ObjectNode userToJson( final ObjectNode json, final UserModel user )
    {
        json.put( "photo", user.getPhoto() );
        json.put( "lastLogged", user.getLastLogged() );
        json.put( "created", user.getCreated() );
        json.put( "email", user.getEmail() );
        final ObjectNode userInfojson = userInfoToJson( user.getUserInfo() );
        json.put( "userInfo", userInfojson );

        final List<Map<String, String>> groups = user.getGroups();
        final ArrayNode groupsJson = arrayNode();
        json.put( "groups", groupsJson );
        for ( Map<String, String> groupItem : groups )
        {
            final ObjectNode groupJson = objectNode();
            for ( String propName : groupItem.keySet() )
            {
                groupJson.put( propName, groupItem.get( propName ) );
            }
            groupsJson.add( groupJson );
        }

//        "graph": [ É ],

        return json;
    }

    private ObjectNode userInfoToJson( final UserInfoModel userInfo )
    {
        final ObjectNode json = objectNode();
        json.put( "firstName", userInfo.getFirstName() );
        json.put( "lastName", userInfo.getLastName() );
        json.put( "middleName", userInfo.getMiddleName() );
        json.put( "birthday", userInfo.getBirthday() );
        json.put( "country", userInfo.getCountry() );
        json.put( "description", userInfo.getDescription() );
        json.put( "initials", userInfo.getInitials() );
        json.put( "globalPosition", userInfo.getGlobalPosition() );
        json.put( "htmlEmail", userInfo.getHtmlEmail() );
        json.put( "locale", userInfo.getLocale() );
        json.put( "nickName", userInfo.getNickName() );
        json.put( "personalId", userInfo.getPersonalId() );
        json.put( "memberId", userInfo.getMemberId() );
        json.put( "organization", userInfo.getOrganization() );
        json.put( "prefix", userInfo.getPrefix() );
        json.put( "suffix", userInfo.getSuffix() );
        json.put( "title", userInfo.getTitle() );
        json.put( "homePage", userInfo.getHomePage() );
        json.put( "mobile", userInfo.getMobile() );
        json.put( "phone", userInfo.getPhone() );
        json.put( "fax", userInfo.getFax() );
        json.put( "gender", userInfo.getGender() );
        json.put( "timezone", userInfo.getTimeZone() );

        final ArrayNode addressesJson = arrayNode();
        final List<AddressModel> addresses = userInfo.getAddresses();
        for ( AddressModel address : addresses )
        {
            ObjectNode addressJson = addressToJson( address );
            addressesJson.add( addressJson );
        }
        json.put( "addresses", addressesJson );

        return json;
    }

    private ObjectNode addressToJson( final AddressModel address )
    {
        final ObjectNode json = objectNode();
        json.put( "label", address.getLabel() );
        json.put( "street", address.getStreet() );
        json.put( "postalAddress", address.getPostalAddress() );
        json.put( "postalCode", address.getPostalCode() );
        json.put( "region", address.getRegion() );
        json.put( "country", address.getCountry() );
        json.put( "isoRegion", address.getIsoRegion() );
        json.put( "isoCountry", address.getIsoCountry() );
        return json;
    }

    private void putJsonDate( ObjectNode json, String fieldName, Date value )
    {
        final String valueStr = dateFormatter.print( new DateTime( value ) );
        json.put( fieldName, valueStr );
    }
}
