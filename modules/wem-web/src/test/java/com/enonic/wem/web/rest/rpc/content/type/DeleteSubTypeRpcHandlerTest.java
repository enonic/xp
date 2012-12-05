package com.enonic.wem.web.rest.rpc.content.type;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.type.SubTypeDeletionResult;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.exception.SubTypeNotFoundException;
import com.enonic.wem.api.exception.UnableToDeleteSubTypeException;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

public class DeleteSubTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final DeleteSubTypeRpcHandler handler = new DeleteSubTypeRpcHandler();
        client = Mockito.mock( Client.class );
        handler.setClient( client );
        return handler;
    }

    @Test
    public void deleteSingleSubType()
        throws Exception
    {
        final QualifiedSubTypeName existingName = new QualifiedSubTypeName( "my:existingSubType" );

        SubTypeDeletionResult subTypeDeletionResult = new SubTypeDeletionResult();
        subTypeDeletionResult.success( existingName );

        Mockito.when( client.execute( Mockito.any( Commands.subType().delete().getClass() ) ) ).thenReturn( subTypeDeletionResult );

        testSuccess( "deleteSubType_param.json", "deleteSubType_success_result.json" );
    }

    @Test
    public void deleteVariousSubTypes()
        throws Exception
    {
        final QualifiedSubTypeName existingName = new QualifiedSubTypeName( "my:existingSubType" );
        final QualifiedSubTypeName notFoundName = new QualifiedSubTypeName( "my:notFoundSubType" );
        final QualifiedSubTypeName beingUsedName = new QualifiedSubTypeName( "my:beingUsedSubType" );

        SubTypeDeletionResult subTypeDeletionResult = new SubTypeDeletionResult();
        subTypeDeletionResult.success( existingName );
        subTypeDeletionResult.failure( notFoundName, new SubTypeNotFoundException( notFoundName ) );
        subTypeDeletionResult.failure( beingUsedName, new UnableToDeleteSubTypeException( beingUsedName, "Subtype is being used" ) );

        Mockito.when( client.execute( Mockito.any( Commands.subType().delete().getClass() ) ) ).thenReturn( subTypeDeletionResult );

        testSuccess( "deleteSubType_param.json", "deleteSubType_error_result.json" );
    }

}
