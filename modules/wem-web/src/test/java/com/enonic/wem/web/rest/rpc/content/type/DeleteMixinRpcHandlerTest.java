package com.enonic.wem.web.rest.rpc.content.type;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.type.MixinDeletionResult;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.api.exception.MixinNotFoundException;
import com.enonic.wem.api.exception.UnableToDeleteMixinException;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

public class DeleteMixinRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final DeleteMixinRpcHandler handler = new DeleteMixinRpcHandler();
        client = Mockito.mock( Client.class );
        handler.setClient( client );
        return handler;
    }

    @Test
    public void deleteSingleMixin()
        throws Exception
    {
        final QualifiedMixinName existingName = new QualifiedMixinName( "my:existingMixin" );

        MixinDeletionResult mixinDeletionResult = new MixinDeletionResult();
        mixinDeletionResult.success( existingName );

        Mockito.when( client.execute( Mockito.any( Commands.mixin().delete().getClass() ) ) ).thenReturn( mixinDeletionResult );

        testSuccess( "deleteMixin_param.json", "deleteMixin_success_result.json" );
    }

    @Test
    public void deleteVariousMixins()
        throws Exception
    {
        final QualifiedMixinName existingName = new QualifiedMixinName( "my:existingMixin" );
        final QualifiedMixinName notFoundName = new QualifiedMixinName( "my:notFoundMixin" );
        final QualifiedMixinName beingUsedName = new QualifiedMixinName( "my:beingUsedMixin" );

        MixinDeletionResult mixinDeletionResult = new MixinDeletionResult();
        mixinDeletionResult.success( existingName );
        mixinDeletionResult.failure( notFoundName, new MixinNotFoundException( notFoundName ) );
        mixinDeletionResult.failure( beingUsedName, new UnableToDeleteMixinException( beingUsedName, "Mixin is being used" ) );

        Mockito.when( client.execute( Mockito.any( Commands.mixin().delete().getClass() ) ) ).thenReturn( mixinDeletionResult );

        testSuccess( "deleteMixin_param.json", "deleteMixin_error_result.json" );
    }

}
