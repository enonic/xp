package com.enonic.wem.web.rest2.resource.account;

import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.enonic.wem.core.jcr.PageList;
import com.enonic.wem.core.jcr.accounts.Gender;
import com.enonic.wem.core.jcr.accounts.JcrAccount;
import com.enonic.wem.core.jcr.accounts.JcrAddress;
import com.enonic.wem.core.jcr.accounts.JcrGroup;
import com.enonic.wem.core.jcr.accounts.JcrUser;
import com.enonic.wem.core.jcr.accounts.JcrUserInfo;
import com.enonic.wem.web.rest2.common.JsonResult;

public final class AccountsResult
    extends JsonResult
{
    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss" );

    private final PageList<JcrAccount> accountsList;

    public AccountsResult( PageList<JcrAccount> accountsList )
    {
        this.accountsList = accountsList;
    }

    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();
        json.put( "success", true );
        json.put( "results", accountsToJson( this.accountsList ) );
        return json;
    }

    private ObjectNode accountsToJson( final PageList<JcrAccount> accountsList )
    {
        final ObjectNode json = objectNode();
        json.put( "total", accountsList.getTotal() );

        final ArrayNode accountsJson = arrayNode();
        for ( JcrAccount account : accountsList.getList() )
        {
            final ObjectNode resultJson = accountToJson( account );
            accountsJson.add( resultJson );
        }
        json.put( "accounts", accountsJson );

        return json;
    }

    private ObjectNode accountToJson( JcrAccount account )
    {
        final ObjectNode json = objectNode();
        json.put( "type", account.getAccountType().name().toLowerCase() );
        json.put( "key", account.getId() );
        json.put( "name", account.getName() );
        json.put( "qualifiedName", account.getQualifiedName() );
        json.put( "userStore", account.getUserStore() );
        json.put( "displayName", account.getDisplayName() );
        putJsonDate( json, "lastModified", account.getLastModified() );
        json.put( "hasPhoto", account.hasPhoto() );
        json.put( "isEditable", true ); // model.setEditable(!(entity.isAnonymous() || isAdmin));
        json.put( "builtIn", account.isBuiltIn() );

        if ( account.isGroup() )
        {
            groupToJson( (JcrGroup) account, json );
        }
        else if ( account.isUser() )
        {
            userToJson( (JcrUser) account, json );
        }

        return json;
    }

    private ObjectNode groupToJson( final JcrGroup group, final ObjectNode json )
    {
        json.put( "description", group.getDescription() );
        json.put( "membersCount", group.getMembersCount() );

        final Set<JcrAccount> members = group.getMembers();
        final ArrayNode membersJson = arrayNode();
        for ( JcrAccount member : members )
        {
            final ObjectNode memberJson = accountToJson( member );
            membersJson.add( memberJson );
        }
        json.put( "members", membersJson );

        return json;
    }

    private ObjectNode userToJson( final JcrUser user, final ObjectNode json )
    {
        putJsonDate( json, "lastLogged", user.getLastLogged() );
        json.put( "created", user.getCreated() );
        json.put( "email", user.getEmail() );
        final ObjectNode userInfojson = userInfoToJson( user.getUserInfo() );
        json.put( "userInfo", userInfojson );

        final Set<JcrGroup> groups = user.getMemberships();
        final ArrayNode groupsJson = arrayNode();
        for ( JcrGroup groupMembership : groups )
        {
            final ObjectNode groupJson = accountToJson( groupMembership );
            groupsJson.add( groupJson );
        }
        json.put( "groups", groupsJson );

        return json;
    }

    private ObjectNode userInfoToJson( final JcrUserInfo userInfo )
    {
        final ObjectNode json = objectNode();
        json.put( "firstName", userInfo.getFirstName() );
        json.put( "lastName", userInfo.getLastName() );
        json.put( "middleName", userInfo.getMiddleName() );
        putJsonDate( json, "birthday", userInfo.getBirthday() );
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
        final Gender gender = userInfo.getGender();
        json.put( "gender", gender == null ? null : gender.name() );
        json.put( "timezone", userInfo.getTimeZone() );

        final ArrayNode addressesJson = arrayNode();
        final List<JcrAddress> addresses = userInfo.getAddresses();
        for ( JcrAddress address : addresses )
        {
            ObjectNode addressJson = addressToJson( address );
            addressesJson.add( addressJson );
        }
        json.put( "addresses", addressesJson );

        return json;
    }

    private ObjectNode addressToJson( final JcrAddress address )
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

    private void putJsonDate( ObjectNode json, String fieldName, DateTime value )
    {
        final String valueStr = dateFormatter.print( value );
        json.put( fieldName, valueStr );
    }
}
