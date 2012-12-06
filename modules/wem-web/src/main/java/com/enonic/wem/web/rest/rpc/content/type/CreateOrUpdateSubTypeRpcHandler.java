package com.enonic.wem.web.rest.rpc.content.type;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.CreateSubType;
import com.enonic.wem.api.command.content.type.GetSubTypes;
import com.enonic.wem.api.command.content.type.UpdateSubTypes;
import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.core.content.ParsingException;
import com.enonic.wem.core.content.type.SubTypeXmlSerializer;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.subType;
import static com.enonic.wem.api.content.type.editor.SubTypeEditors.setSubType;

@Component
public class CreateOrUpdateSubTypeRpcHandler
    extends AbstractDataRpcHandler
{
    private final SubTypeXmlSerializer subTypeXmlSerializer = new SubTypeXmlSerializer();

    public CreateOrUpdateSubTypeRpcHandler()
    {
        super( "subType_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String subTypeJson = context.param( "subType" ).required().asString();
        final SubType subType;
        try
        {
            subType = subTypeXmlSerializer.toSubType( subTypeJson );
        }
        catch ( ParsingException e )
        {
            context.setResult( new JsonErrorResult( "Invalid sub type format" ) );
            return;
        }

        if ( !subTypeExists( subType.getQualifiedName() ) )
        {
            final CreateSubType createSubType = subType().create().subType( subType );
            client.execute( createSubType );
            context.setResult( CreateOrUpdateSubTypeJsonResult.created() );
        }
        else
        {
            final QualifiedSubTypeNames names = QualifiedSubTypeNames.from( subType.getQualifiedName() );
            final UpdateSubTypes updateSubType = subType().update().names( names ).editor( setSubType( subType ) );
            client.execute( updateSubType );
            context.setResult( CreateOrUpdateSubTypeJsonResult.updated() );
        }
    }

    private boolean subTypeExists( final QualifiedSubTypeName qualifiedName )
    {
        final GetSubTypes getSubTypes = subType().get().names( QualifiedSubTypeNames.from( qualifiedName ) );
        return !client.execute( getSubTypes ).isEmpty();
    }
}
