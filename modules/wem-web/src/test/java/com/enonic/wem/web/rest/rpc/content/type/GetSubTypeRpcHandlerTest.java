package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.type.GetSubTypes;
import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputSubType;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import static com.enonic.wem.api.command.Commands.subType;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_LINE;

public class GetSubTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final GetSubTypeRpcHandler handler = new GetSubTypeRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testRequestGetSubType_existing()
        throws Exception
    {
        final Input inputText1 = newInput().name( "inputText1" ).type( TEXT_LINE ).label( "Line Text 1" ).required( true ).helpText(
            "Help text line 1" ).required( true ).build();
        final SubType subType = InputSubType.newInputSubType().
            module( ModuleName.from( "myModule" ) ).
            input( inputText1 ).
            build();

        final SubTypes subTypes = SubTypes.from( subType );
        final QualifiedSubTypeNames names = QualifiedSubTypeNames.from( new QualifiedSubTypeName( "myModule:mysubtype" ) );
        Mockito.when( client.execute( subType().get().names( names ) ) ).thenReturn( subTypes );

        testSuccess( "getSubType_param.json", "getSubType_result.json" );
    }

    @Test
    public void testRequestGetSubType_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetSubTypes.class ) ) ).thenReturn( SubTypes.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "error", "Sub type [myModule:mysubtype] was not found" );
        testSuccess( "getSubType_param.json", resultJson );
    }
}