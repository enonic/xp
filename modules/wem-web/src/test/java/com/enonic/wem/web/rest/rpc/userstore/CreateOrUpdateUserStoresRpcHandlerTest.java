package com.enonic.wem.web.rest.rpc.userstore;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.userstore.CreateUserStore;
import com.enonic.wem.api.command.userstore.GetUserStores;
import com.enonic.wem.api.command.userstore.UpdateUserStores;
import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.UserStores;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

public class CreateOrUpdateUserStoresRpcHandlerTest
    extends AbstractRpcHandlerTest

{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        final CreateOrUpdateUserStoreRpcHandler handler = new CreateOrUpdateUserStoreRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void createUserStoreNoConfig()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetUserStores.class ) ) ).thenReturn( UserStores.empty() );
        Mockito.when( client.execute( Mockito.isA( CreateUserStore.class ) ) ).thenReturn( UserStoreName.from( "store1" ) );

        testSuccess( createParams( false, null, "Connector v1.23", "store1" ), createResult( true, false, 1 ) );
    }

    @Test
    public void createUserStore()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetUserStores.class ) ) ).thenReturn( UserStores.empty() );
        Mockito.when( client.execute( Mockito.isA( CreateUserStore.class ) ) ).thenReturn( UserStoreName.from( "store1" ) );

        testSuccess( createParams( "store1" ), createResult( true, false, 1 ) );
    }

    @Test
    public void updateSingleUserStore()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetUserStores.class ) ) ).thenReturn( createUserStores( "store1" ) );
        Mockito.when( client.execute( Mockito.isA( UpdateUserStores.class ) ) ).thenReturn( 1 );

        testSuccess( createParams( "store1" ), createResult( false, true, 1 ) );
    }


    @Test
    public void updateManyUserStores()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetUserStores.class ) ) ).thenReturn( createUserStores( "store1", "store2", "store3" ) );
        Mockito.when( client.execute( Mockito.isA( UpdateUserStores.class ) ) ).thenReturn( 3 );

        testSuccess( createParams( "store1", "store2", "store3" ), createResult( false, true, 3 ) );
    }


    private ObjectNode createParams( final boolean isDefault, final String config, String connector, final String... name )
    {
        final ObjectNode params = objectNode();

        if ( name.length == 1 )
        {
            params.put( "name", name[0] );
        }
        else if ( name.length > 1 )
        {
            ArrayNode names = params.putArray( "name" );
            for ( final String aName : name )
            {
                names.add( aName );
            }
        }

        params.put( "defaultUserstore", isDefault );
        if ( config != null )
        {
            params.put( "config", config );
        }
        if ( connector != null )
        {
            params.put( "connector", connector );
        }
        params.putArray( "administrators" );

        return params;
    }

    private ObjectNode createParams( final String... name )
    {
        return createParams( false, "<config><user-fields></user-fields></config>", "Connector v1.23", name );
    }

    private ObjectNode createResult( final boolean created, final boolean updated, final int count )
    {
        final ObjectNode result = objectNode();
        result.put( "success", true );
        result.put( "created", created );
        result.put( "updated", updated );
        result.put( "count", count );

        return result;
    }

    private UserStores createUserStores( final String... name )
    {
        List<UserStore> userStoreList = new ArrayList<UserStore>();
        for ( final String aName : name )
        {
            UserStore userStore = new UserStore( UserStoreName.from( aName ) );
            userStoreList.add( userStore );
        }
        return UserStores.from( userStoreList );
    }
}
