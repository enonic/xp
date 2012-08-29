package com.enonic.wem.web.rest2.resource.userstore;

import java.util.Collection;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.web.rest2.common.JsonResult;
import com.enonic.wem.web.rest2.resource.account.AccountUriHelper;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;

public class UserStoreDetailsResult
    extends JsonResult
{
    public static final String USERSTORE_SYSTEM = "system";

    public static final String TYPE_USER = "user";

    public static final String TYPE_GROUP = "group";

    public static final String TYPE_ROLE = "role";

    private final UserStoreEntity userStore;

    private final List<GroupEntity> groups;

    private final List<UserEntity> users;

    private final UserStoreConnectorConfig connectorConfig;


    public UserStoreDetailsResult( final UserStoreEntity userStore, final UserStoreConnectorConfig connectorConfig,
                                   final List<GroupEntity> groups, final List<UserEntity> users )
    {
        this.userStore = userStore;
        this.connectorConfig = connectorConfig;
        this.groups = groups;
        this.users = users;
    }

    @Override
    public JsonNode toJson()
    {
        ObjectNode json = objectNode();
        if ( userStore != null )
        {
            json.put( "key", userStore.getKey().toString() );
            json.put( "name", userStore.getName() );
            json.put( "default", userStore.isDefaultUserStore() );
            json.put( "connector", userStore.getConnectorName() );
            json.put( "configXML", JDOMUtil.serialize( userStore.getConfigAsXMLDocument(), 2, true ) );

            if ( userStore.getConfig() != null )
            {
                json.put( "userFields", toJson( userStore.getConfig().getUserFieldConfigs() ) );
            }

            if ( connectorConfig != null )
            {
                json.put( "plugin", connectorConfig.getPluginType() );
                json.put( "userPolicy", getUserPolicy( connectorConfig ) );
                json.put( "groupPolicy", getGroupPolicy( connectorConfig ) );
            }

            json.put( "userCount", users == null ? 0 : users.size() );
            json.put( "groupCount", groups == null ? 0 : groups.size() );
            json.put( "administrators", getAdministrators( groups ) );
        }
        return json;
    }

    private ArrayNode toJson( final Collection<UserStoreUserFieldConfig> fieldConfigs )
    {
        ArrayNode jsons = arrayNode();
        for ( UserStoreUserFieldConfig config : fieldConfigs )
        {
            ObjectNode json = objectNode();

            json.put( "type", convertToCamelCase( config.getType().getName() ) );
            json.put( "readOnly", config.isReadOnly() );
            json.put( "remote", config.isRemote() );
            json.put( "required", config.isRequired() );
            json.put( "iso", config.useIso() );

            jsons.add( json );
        }
        return jsons;
    }

    private ObjectNode toJson( UserEntity user )
    {
        final ObjectNode json = objectNode();

        final UserStoreEntity userstore = user.getUserStore();
        final boolean isAdmin = userstore != null && user.isEnterpriseAdmin() && USERSTORE_SYSTEM.equals( userstore.getName() );
        final boolean isAnonym = user.isAnonymous();
        final String key = String.valueOf( user.getKey() );

        json.put( "key", key );
        json.put( "type", TYPE_USER );
        json.put( "name", user.getName() );
        json.put( "email", user.getEmail() );
        json.put( "userStore", userstore != null ? userstore.getName() : "null" );
        json.put( "qualifiedName", String.valueOf( user.getQualifiedName() ) );
        json.put( "displayName", user.getDisplayName() );
        json.put( "lastModified", "2011-08-09 08:23:07" );          //TODO
        json.put( "created", "2011-08-09 08:23:07" );           //TODO
        json.put( "builtIn", user.isBuiltIn() );
        json.put( "editable", !( isAnonym || isAdmin ) );
        json.put( "info_uri", AccountUriHelper.getAccountInfoUri( AccountType.USER, key ) );
        json.put( "image_uri", AccountUriHelper.getAccountImageUri( user ) );
        json.put( "graph_uri", AccountUriHelper.getAccountGraphUri( key ) );
        return json;
    }

    private ObjectNode toJson( GroupEntity group )
    {
        final ObjectNode json = objectNode();

        final boolean builtIn = group.isBuiltIn();
        final boolean isAuth = GroupType.AUTHENTICATED_USERS.equals( group.getType() );
        final boolean isAnonym = GroupType.ANONYMOUS.equals( group.getType() );
        final String key = String.valueOf( group.getGroupKey() );

        json.put( "key", key );
        json.put( "type", builtIn ? TYPE_ROLE : TYPE_GROUP );
        json.put( "name", group.getName() );
        json.put( "userStore", group.getUserStore() != null ? group.getUserStore().getName() : "null" );
        json.put( "qualifiedName", String.valueOf( group.getQualifiedName() ) );
        json.put( "displayName", group.getName() );        //TODO: temporary solution
        json.put( "lastModified", "2012-07-24 16:18:35" );          //TODO
        json.put( "created", "2012-07-24 16:18:35" );           //TODO
        json.put( "builtIn", builtIn );
        json.put( "editable", !( isAuth || isAnonym ) );

        final AccountType accountType = builtIn ? AccountType.ROLE : AccountType.GROUP;
        json.put( "image_uri", AccountUriHelper.getAccountImageUri( group ) );
        json.put( "info_uri", AccountUriHelper.getAccountInfoUri( accountType, key ) );
        json.put( "graph_uri", AccountUriHelper.getAccountGraphUri( key ) );

        return json;
    }

    private String convertToCamelCase( String input )
    {
        Pattern pattern = Pattern.compile( "-[A-Za-z]" );
        Matcher matcher = pattern.matcher( input );

        while ( matcher.find() )
        {
            MatchResult result = matcher.toMatchResult();
            String token = result.group();
            input = input.replace( token, token.substring( 1 ).toUpperCase() );
        }
        return input;
    }

    private ArrayNode getAdministrators( final List<GroupEntity> groups )
    {
        ArrayNode jsons = arrayNode();
        for ( GroupEntity group : groups )
        {
            if ( group.getType() == GroupType.USERSTORE_ADMINS )
            {
                for ( GroupEntity admin : group.getMembers( true ) )
                {
                    if ( admin.getType().equals( GroupType.USER ) && admin.getUser() != null )
                    {
                        jsons.add( toJson( admin.getUser() ) );
                    }
                    else
                    {
                        jsons.add( toJson( admin ) );
                    }
                }
            }
        }
        return jsons;
    }

    private String getGroupPolicy( UserStoreConnectorConfig connectorConfig )
    {
        if ( connectorConfig == null )
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if ( connectorConfig.canReadGroup() )
        {
            sb.append( "read, " );
        }
        if ( connectorConfig.canCreateGroup() )
        {
            sb.append( "create, " );
        }
        if ( connectorConfig.canUpdateGroup() )
        {
            sb.append( "update, " );
        }
        if ( connectorConfig.canDeleteGroup() )
        {
            sb.append( "delete, " );
        }
        return sb.length() > 2 ? sb.substring( 0, sb.length() - 2 ) : sb.toString();
    }

    private String getUserPolicy( UserStoreConnectorConfig connectorConfig )
    {
        if ( connectorConfig == null )
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if ( connectorConfig.canCreateUser() )
        {
            sb.append( "create, " );
        }
        if ( connectorConfig.canUpdateUser() )
        {
            sb.append( "update, " );
        }
        if ( connectorConfig.canUpdateUserPassword() )
        {
            sb.append( "update password, " );
        }
        if ( connectorConfig.canDeleteUser() )
        {
            sb.append( "delete, " );
        }
        return sb.length() > 2 ? sb.substring( 0, sb.length() - 2 ) : sb.toString();
    }
}
