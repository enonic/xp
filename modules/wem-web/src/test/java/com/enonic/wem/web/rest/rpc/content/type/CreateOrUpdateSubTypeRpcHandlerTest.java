package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.type.CreateSubType;
import com.enonic.wem.api.command.content.type.GetSubTypes;
import com.enonic.wem.api.command.content.type.UpdateSubTypes;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputSubType;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CreateOrUpdateSubTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final CreateOrUpdateSubTypeRpcHandler handler = new CreateOrUpdateSubTypeRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testCreateSubType()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetSubTypes.class ) ) ).thenReturn( SubTypes.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateSubType_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( CreateSubType.class ) );
    }

    @Test
    public void testUpdateSubType()
        throws Exception
    {
        final Input input = newInput().name( "someInput" ).type( InputTypes.TEXT_LINE ).build();
        final SubType existingSubType = InputSubType.newInputSubType().input( input ).module( Module.SYSTEM.getName() ).build();
        final SubTypes subTypes = SubTypes.from( existingSubType );
        Mockito.when( client.execute( isA( GetSubTypes.class ) ) ).thenReturn( subTypes );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateSubType_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( UpdateSubTypes.class ) );
    }

}