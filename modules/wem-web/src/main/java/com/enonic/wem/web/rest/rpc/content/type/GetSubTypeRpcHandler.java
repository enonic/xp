package com.enonic.wem.web.rest.rpc.content.type;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public class GetSubTypeRpcHandler
    extends AbstractDataRpcHandler
{

    public GetSubTypeRpcHandler()
    {
        super( "subType_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final QualifiedSubTypeName subTypeName = new QualifiedSubTypeName( context.param( "subType" ).required().asString() );

        final SubType subType = fetchSubType( subTypeName );

        if ( subType != null )
        {
            context.setResult( new GetSubTypeRpcJsonResult( subType ) );
        }
        else
        {
            context.setResult( new JsonErrorResult( "Sub type [{0}] was not found", subTypeName ) );
        }
    }

    private SubType fetchSubType( final QualifiedSubTypeName subTypeName )
    {
        final QualifiedSubTypeNames names = QualifiedSubTypeNames.from( subTypeName );
        final SubTypes subTypesResult = client.execute( Commands.subType().get().names( names ) );
        return subTypesResult.isEmpty() ? null : subTypesResult.getFirst();
    }
}
