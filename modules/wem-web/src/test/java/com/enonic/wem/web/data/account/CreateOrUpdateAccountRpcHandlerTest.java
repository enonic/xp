package com.enonic.wem.web.data.account;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.codehaus.jackson.node.ObjectNode;
import org.elasticsearch.common.collect.Lists;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Files;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.api.command.account.CreateAccount;
import com.enonic.wem.api.command.account.FindAccounts;
import com.enonic.wem.api.command.account.UpdateAccounts;
import com.enonic.wem.web.data.AbstractRpcHandlerTest;
import com.enonic.wem.web.jsonrpc.JsonRpcHandler;
import com.enonic.wem.web.rest2.service.upload.UploadItem;
import com.enonic.wem.web.rest2.service.upload.UploadService;

public class CreateOrUpdateAccountRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    private UploadService uploadService;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final CreateOrUpdateAccountRpcHandler handler = new CreateOrUpdateAccountRpcHandler();

        uploadService = Mockito.mock( UploadService.class );
        handler.setUploadService( uploadService );
        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testRequestCreateAccount()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( UpdateAccounts.class ) ) ).thenReturn( 1 );
        Mockito.when( client.execute( Mockito.any( FindAccounts.class ) ) ).thenReturn(
            new AccountResult( 0, Collections.<Account>emptyList() ) );
        uploadFile( "01d0cc1d-ac2a-4952-a423-295cc9756bba", "photo.png", "IMAGEDATA".getBytes(), "image/png" );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateUser_param.json", resultJson );
    }

    @Test
    public void testRequestUpdateUser()
        throws Exception
    {
        final AccountKey key = AccountKey.user( "enonic:user1" );
        final UserAccount user = UserAccount.create( key );
        Mockito.when( client.execute( Mockito.any( CreateAccount.class ) ) ).thenReturn( key );

        final AccountResult accountResult = new AccountResult( 1, Lists.<Account>newArrayList( user ) );
        Mockito.when( client.execute( Mockito.any( FindAccounts.class ) ) ).thenReturn( accountResult );
        uploadFile( "01d0cc1d-ac2a-4952-a423-295cc9756bba", "photo.png", "IMAGEDATA".getBytes(), "image/png" );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false);
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateUser_param.json", resultJson);
    }

    @Test
    public void testRequestUpdateGroup()
        throws Exception
    {
        final AccountKey key = AccountKey.group( "enonic:group2" );
        final GroupAccount group = GroupAccount.create( key );
        Mockito.when( client.execute( Mockito.any( CreateAccount.class ) ) ).thenReturn( key );

        final AccountResult accountResult = new AccountResult( 1, Lists.<Account>newArrayList( group ) );
        Mockito.when( client.execute( Mockito.any( FindAccounts.class ) ) ).thenReturn( accountResult );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false);
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateGroup_param.json", resultJson);
    }

    private void uploadFile( final String id, final String name, final byte[] data, final String type )
        throws Exception
    {
        final File file = createTempFile( data );
        final UploadItem item = Mockito.mock( UploadItem.class );
        Mockito.when( item.getId() ).thenReturn( id );
        Mockito.when( item.getMimeType() ).thenReturn( type );
        Mockito.when( item.getUploadTime() ).thenReturn( 0L );
        Mockito.when( item.getName() ).thenReturn( name );
        Mockito.when( item.getSize() ).thenReturn( (long) data.length );
        Mockito.when( item.getFile() ).thenReturn( file );
        Mockito.when( this.uploadService.getItem( Mockito.<String>any() ) ).thenReturn( item );
    }

    private File createTempFile( final byte[] data )
        throws IOException
    {
        final String id = UUID.randomUUID().toString();
        final File file = File.createTempFile( id, "" );
        Files.write( data, file );
        return file;
    }
}
