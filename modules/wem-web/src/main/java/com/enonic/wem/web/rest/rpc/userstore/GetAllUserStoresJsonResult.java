package com.enonic.wem.web.rest.rpc.userstore;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.api.userstore.config.UserStoreConfigSerializer;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.statistics.UserStoreStatistics;
import com.enonic.wem.web.json.JsonResult;

class GetAllUserStoresJsonResult
    extends JsonResult
{
    private UserStores userStores;

    public GetAllUserStoresJsonResult( UserStores userStores )
    {
        this.userStores = userStores;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", userStores.getSize() );
        json.put( "userStores", serialize( userStores.getList() ) );
    }

    private ArrayNode serialize( final List<UserStore> userStoreList )
    {
        final ArrayNode jsonUserStores = arrayNode();
        for ( UserStore userStore : userStoreList )
        {
            final ObjectNode jsonUserStore = objectNode();
            jsonUserStore.put( "name", userStore.getName().toString() );
            jsonUserStore.put( "defaultStore", userStore.isDefaultStore() );
            jsonUserStore.put( "connectorName", userStore.getConnectorName() );

            if ( userStore.getConfig() != null )
            {
                jsonUserStore.put( "configXML", new UserStoreConfigSerializer().toXmlString( userStore.getConfig() ) );
                jsonUserStore.put( "userFields", toJson( userStore.getConfig().getFields() ) );
            }
            final UserStoreConnector connectorConfig = userStore.getConnector();
            if ( connectorConfig != null )
            {
                jsonUserStore.put( "plugin", connectorConfig.getPluginClass() );
                jsonUserStore.put( "userPolicy", getUserPolicy( connectorConfig ) );
                jsonUserStore.put( "groupPolicy", getGroupPolicy( connectorConfig ) );
            }
            final UserStoreStatistics connectorStats = userStore.getStatistics();
            if ( connectorStats != null )
            {
                jsonUserStore.put( "userCount", connectorStats.getNumUsers() );
                jsonUserStore.put( "groupCount", connectorStats.getNumGroups() );
                jsonUserStore.put( "roleCount", connectorStats.getNumRoles() );
            }
            final AccountKeys administrators = userStore.getAdministrators();
            if ( administrators != null )
            {
                jsonUserStore.put( "administrators", getAdministrators( administrators ) );
            }

            jsonUserStores.add( jsonUserStore );
        }

        return jsonUserStores;
    }

    private ArrayNode toJson( final List<UserStoreFieldConfig> fieldConfigs )
    {
        ArrayNode jsons = arrayNode();
        for ( UserStoreFieldConfig config : fieldConfigs )
        {
            ObjectNode json = objectNode();

            json.put( "type", convertToCamelCase( config.getName() ) );
            json.put( "readOnly", config.isReadOnly() );
            json.put( "remote", config.isRemote() );
            json.put( "required", config.isRequired() );
            json.put( "iso", config.isIso() );

            jsons.add( json );
        }
        return jsons;
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

    private ArrayNode getAdministrators( final AccountKeys accounts )
    {
        ArrayNode jsons = arrayNode();
        for ( AccountKey account : accounts.getSet() )
        {
            jsons.add( account.toString() );
        }
        return jsons;
    }

    private ObjectNode getGroupPolicy( UserStoreConnector connectorConfig )
    {
        ObjectNode json = objectNode();
        if ( connectorConfig != null )
        {
            json.put( "create", connectorConfig.isCreateGroup() );
            json.put( "read", connectorConfig.isReadGroup() );
            json.put( "update", connectorConfig.isUpdateGroup() );
            json.put( "delete", connectorConfig.isDeleteGroup() );
        }
        return json;
    }

    private ObjectNode getUserPolicy( UserStoreConnector connectorConfig )
    {
        ObjectNode json = objectNode();
        if ( connectorConfig != null )
        {
            json.put( "create", connectorConfig.isCreateUser() );
            json.put( "updatePassword", connectorConfig.isUpdatePassword() );
            json.put( "update", connectorConfig.isUpdateUser() );
            json.put( "delete", connectorConfig.isDeleteUser() );
        }
        return json;
    }
}
