package com.enonic.wem.web.rest.rpc.content;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Files;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.CreateAccount;
import com.enonic.wem.api.command.account.GetAccounts;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.web.rest.service.upload.UploadItem;

public class CreateOrUpdateContentRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final CreateOrUpdateContentRpcHandler handler = new CreateOrUpdateContentRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    @Ignore
    public void testRequestCreateContent()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( UpdateContents.class ) ) ).thenReturn( 1 );
        Mockito.when( client.execute( Mockito.any( GetContents.class ) ) ).thenReturn( Contents.empty() );
        //Mockito.when( client.execute( Commands.content().findMemberships().key( AccountKey.user( "enonic:user1" ) ) ) ).thenReturn(
        //    AccountKeys.empty() );
        uploadFile( "01d0cc1d-ac2a-4952-a423-295cc9756bba", "photo.png", "IMAGEDATA".getBytes(), "image/png" );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateUser_param.json", resultJson );
    }

    @Test
    @Ignore
    public void testRequestUpdateUser()
        throws Exception
    {
        //final ContentKey key = ContentKey.user( "enonic:user1" );
        //final UserAccount user = UserAccount.create( key );
        //Mockito.when( client.execute( Mockito.any( CreateAccount.class ) ) ).thenReturn( key );

        //final Accounts accountResult = Accounts.from( user );
        //Mockito.when( client.execute( Mockito.any( GetAccounts.class ) ) ).thenReturn( accountResult );
        uploadFile( "01d0cc1d-ac2a-4952-a423-295cc9756bba", "photo.png", "IMAGEDATA".getBytes(), "image/png" );

        Mockito.when( client.execute( Commands.account().findMemberships().key( AccountKey.user( "enonic:user1" ) ) ) ).thenReturn(
            AccountKeys.from( "role:enonic:contributors", "group:enonic:group999" ) );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateUser_param.json", resultJson );
    }

    @Test
    @Ignore
    public void testRequestUpdateGroup()
        throws Exception
    {
        final AccountKey key = AccountKey.group( "enonic:group2" );
        final GroupAccount group = GroupAccount.create( key );
        Mockito.when( client.execute( Mockito.any( CreateAccount.class ) ) ).thenReturn( key );

        final Accounts accountResult = Accounts.from( group );
        Mockito.when( client.execute( Mockito.any( GetAccounts.class ) ) ).thenReturn( accountResult );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateGroup_param.json", resultJson );
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
