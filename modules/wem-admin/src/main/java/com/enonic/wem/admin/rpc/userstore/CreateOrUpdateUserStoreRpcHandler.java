package com.enonic.wem.admin.rpc.userstore;


import com.google.common.base.Strings;

import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.exception.UserStoreNotFoundException;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreConfigParser;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.editor.UserStoreEditor;
import com.enonic.wem.api.userstore.editor.UserStoreEditors;


public class CreateOrUpdateUserStoreRpcHandler
    extends AbstractDataRpcHandler
{
    public CreateOrUpdateUserStoreRpcHandler()
    {
        super( "userstore_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final UserStoreNames userStoreNames = UserStoreNames.from( context.param( "name" ).required().asStringArray() );
        UserStores userStores;
        try
        {
            userStores = client.execute( Commands.userStore().get().names( userStoreNames ) );
        }
        catch ( UserStoreNotFoundException e )
        {
            userStores = UserStores.empty();
        }

        final UserStore userStore = getUserStoreFromRequest( context, userStores, userStoreNames.getFirst() );

        if ( !userStores.isEmpty() )
        {
            for ( UserStoreName userStoreName : userStoreNames )
            {
                final UserStoreEditor userStoreEditor = UserStoreEditors.setUserStore( userStore );
                this.client.execute( Commands.userStore().update().name( userStoreName ).editor( userStoreEditor ) );
            }
            context.setResult( CreateOrUpdateUserStoreJsonResult.updated() );
        }
        else
        {
            this.client.execute( Commands.userStore().create().userStore( userStore ) );
            context.setResult( CreateOrUpdateUserStoreJsonResult.created() );
        }
    }


    private UserStore getUserStoreFromRequest( final JsonRpcContext context, final UserStores userStores,
                                               final UserStoreName userStoreName )
        throws Exception
    {
        UserStore userStore;
        if ( !userStores.isEmpty() )
        {
            userStore = userStores.first();
        }
        else
        {
            userStore = new UserStore( userStoreName );
        }

        userStore.setDefaultStore( context.param( "defaultUserstore" ).asBoolean( false ) );

        UserStoreConfig config;
        String configParam = context.param( "configXML" ).asString();
        if ( Strings.isNullOrEmpty( configParam ) )
        {
            config = new UserStoreConfig();
        }
        else
        {
            try
            {
                config = new UserStoreConfigParser().parseXml( configParam );
            }
            catch ( Exception e )
            {
                config = new UserStoreConfig();
            }
        }
        userStore.setConfig( config );

        String connectorName = context.param( "connectorName" ).required().asString();
        userStore.setConnectorName( connectorName );
        userStore.setConnector( new UserStoreConnector( connectorName ) );

        String[] administrators = context.param( "administrators" ).asStringArray();
        userStore.setAdministrators( AccountKeys.from( administrators ) );

        return userStore;
    }
}
