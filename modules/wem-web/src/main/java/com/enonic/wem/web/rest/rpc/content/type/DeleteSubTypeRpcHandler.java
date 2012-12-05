package com.enonic.wem.web.rest.rpc.content.type;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.type.DeleteSubTypes;
import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypeDeletionResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public class DeleteSubTypeRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteSubTypeRpcHandler()
    {
        super( "subType_delete" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final QualifiedSubTypeNames subTypeNames =
            QualifiedSubTypeNames.from( context.param( "qualifiedSubTypeNames" ).required().asStringArray() );

        final DeleteSubTypes deleteSubTypes = Commands.subType().delete().names( subTypeNames );
        final SubTypeDeletionResult deletionResult = client.execute( deleteSubTypes );
        context.setResult( new DeleteSubTypeJsonResult( deletionResult ) );
    }
}
