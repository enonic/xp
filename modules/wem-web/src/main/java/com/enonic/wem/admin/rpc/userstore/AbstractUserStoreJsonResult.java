package com.enonic.wem.admin.rpc.userstore;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.config.UserStoreConfigSerializer;
import com.enonic.wem.api.userstore.config.UserStoreFieldConfig;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.statistics.UserStoreStatistics;


public abstract class AbstractUserStoreJsonResult
    extends JsonResult
{

    protected AbstractUserStoreJsonResult()
    {
        super( true );
    }


    protected void serializeUserStore( ObjectNode json, UserStore userStore )
    {
        if ( json == null || userStore == null )
        {
            return;
        }
        json.put( "name", userStore.getName().toString() );
        json.put( "defaultStore", userStore.isDefaultStore() );
        json.put( "connectorName", userStore.getConnectorName() );

        if ( userStore.getConfig() != null )
        {
            json.put( "configXML", new UserStoreConfigSerializer().toXmlString( userStore.getConfig() ) );
            serializeUserFields( json.putArray( "userFields" ), userStore.getConfig().getFields() );
        }
        final UserStoreConnector connectorConfig = userStore.getConnector();
        if ( connectorConfig != null )
        {
            json.put( "plugin", connectorConfig.getPluginClass() );
            json.put( "userPolicy", getUserPolicy( connectorConfig ) );
            json.put( "groupPolicy", getGroupPolicy( connectorConfig ) );
        }
        final UserStoreStatistics connectorStats = userStore.getStatistics();
        if ( connectorStats != null )
        {
            json.put( "userCount", connectorStats.getNumUsers() );
            json.put( "groupCount", connectorStats.getNumGroups() );
            json.put( "roleCount", connectorStats.getNumRoles() );
        }
        final AccountKeys administrators = userStore.getAdministrators();
        if ( administrators != null )
        {
            json.put( "administrators", getAdministrators( administrators ) );
        }
    }


    private void serializeUserFields( ArrayNode jsons, final List<UserStoreFieldConfig> fieldConfigs )
    {
        if ( jsons == null || fieldConfigs == null )
        {
            return;
        }
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
