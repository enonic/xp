package com.enonic.wem.web.rest.rpc.userstore;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStoreNames;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreConfigParser;
import com.enonic.wem.api.userstore.connector.UserStoreConnector;
import com.enonic.wem.api.userstore.editor.UserStoreEditor;
import com.enonic.wem.api.userstore.editor.UserStoreEditors;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

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
        UserStores userStores = client.execute( Commands.userStore().get().names( userStoreNames ) );

        final UserStore userStore = getUserStoreFromRequest( context, userStores, userStoreNames.getFirst() );

        if ( !userStores.isEmpty() )
        {
            final UserStoreEditor userStoreEditor = UserStoreEditors.setUserStore( userStore );
            this.client.execute( Commands.userStore().update().names( userStoreNames ).editor( userStoreEditor ) );
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
            userStore = userStores.getFirst();
        }
        else
        {
            userStore = new UserStore( userStoreName );
        }

        userStore.setDefaultStore( context.param( "defaultUserstore" ).asBoolean( false ) );

        UserStoreConfig config;
        if ( context.param( "config" ).isNull() )
        {
            config = new UserStoreConfig();
        }
        else
        {
            config = new UserStoreConfigParser().parseXml( context.param( "config" ).asString() );
        }
        userStore.setConfig( config );

        String connectorName = context.param( "connector" ).required().asString();
        userStore.setConnectorName( connectorName );
        userStore.setConnector( new UserStoreConnector( connectorName ) );

        String[] administrators = context.param( "administrators" ).asStringArray();
        userStore.setAdministrators( AccountKeys.from( administrators ) );

        return userStore;
    }
}
