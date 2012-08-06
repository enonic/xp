package com.enonic.wem.web.rest2.resource.account.user;

import java.text.SimpleDateFormat;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.core.search.UserInfoHelper;
import com.enonic.wem.web.rest2.common.JsonResult;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

public final class UserResult
    extends JsonResult
{

    public static final String USERSTORE_SYSTEM = "system";

    public static final String TYPE_USER = "user";

    public static final String TYPE_GROUP = "group";

    public static final String TYPE_ROLE = "role";

    private UserEntity user;

    public UserResult( final UserEntity user )
    {
        this.user = user;
    }

    @Override
    public JsonNode toJson()
    {
        return toJson( user );
    }

    private JsonNode toJson( UserEntity user )
    {
        if ( user == null )
        {
            return null;
        }

        final ObjectNode json = objectNode();

        UserStoreEntity userstore = user.getUserStore();
        boolean isAdmin = userstore != null && user.isEnterpriseAdmin() && USERSTORE_SYSTEM.equals( userstore.getName() );
        boolean isAnonym = user.isAnonymous();

        json.put( "key", String.valueOf( user.getKey() ) );
        json.put( "type", TYPE_USER );
        json.put( "name", user.getName() );
        json.put( "email", user.getEmail() );
        json.put( "userStore", userstore != null ? userstore.getName() : "null" );
        json.put( "qualifiedName", String.valueOf( user.getQualifiedName() ) );
        json.put( "displayName", user.getDisplayName() );
        json.put( "lastModified", "2011-08-09 08:23:07" );          //TODO
        json.put( "created", "2011-08-09 08:23:07" );           //TODO
        json.put( "hasPhoto", user.hasPhoto() );
        json.put( "builtIn", user.isBuiltIn() );
        json.put( "editable", !( isAnonym || isAdmin ) );

        json.put( "info", toJson( UserInfoHelper.toUserInfo( user ) ) );

        json.put( "memberships", toJson( user.getAllMemberships() ) );

        return json;
    }

    private JsonNode toJson( final UserInfo userInfo )
    {
        if ( userInfo == null )
        {
            return null;
        }

        ObjectNode json = objectNode();

        json.put( "firstName", userInfo.getFirstName() );
        json.put( "lastName", userInfo.getLastName() );
        json.put( "middleName", userInfo.getMiddleName() );
        String birthday = null;
        if ( userInfo.getBirthday() != null )
        {
            birthday = new SimpleDateFormat( "yyyy-MM-dd" ).format( userInfo.getBirthday() );
        }
        json.put( "birthday", birthday );
        json.put( "country", userInfo.getCountry() );
        json.put( "description", userInfo.getDescription() );
        json.put( "initials", userInfo.getInitials() );
        json.put( "globalPosition", userInfo.getGlobalPosition() );
        json.put( "htmlEmail", userInfo.getHtmlEmail() );
        json.put( "locale", userInfo.getLocale() != null ? userInfo.getLocale().getDisplayName() : null );
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
        json.put( "gender", userInfo.getGender() != null ? userInfo.getGender().name() : null );
        json.put( "addresses", toJson( userInfo.getAddresses() ) );
        json.put( "timezone", userInfo.getTimeZone() != null ? userInfo.getTimeZone().getDisplayName() : null );

        return json;
    }

    private JsonNode toJson( final Address[] addresses )
    {
        ArrayNode jsons = arrayNode();
        for ( Address address : addresses )
        {
            ObjectNode json = objectNode();

            json.put( "label", address.getLabel() );
            json.put( "postalAddress", address.getPostalAddress() );
            json.put( "postalCode", address.getPostalCode() );
            json.put( "street", address.getStreet() );
            json.put( "country", address.getCountry() );
            json.put( "isoCountry", address.getIsoCountry() );
            json.put( "region", address.getRegion() );
            json.put( "isoRegion", address.getIsoRegion() );

            jsons.add( json );
        }
        return jsons;
    }

    private JsonNode toJson( final Set<GroupEntity> groups )
    {
        ArrayNode jsons = arrayNode();
        for ( GroupEntity group : groups )
        {
            ObjectNode json = objectNode();

            json.put( "key", group.getGroupKey().toString() );
            json.put( "type", group.isBuiltIn() ? TYPE_ROLE : TYPE_GROUP );
            json.put( "qualifiedName", group.getQualifiedName() != null ? group.getQualifiedName().toString() : null );
            json.put( "name", group.getName() );
            json.put( "displayName", "Display name" );      //TODO
            json.put( "builtIn", group.isBuiltIn() );
            jsons.add( json );
        }
        return jsons;
    }
}
