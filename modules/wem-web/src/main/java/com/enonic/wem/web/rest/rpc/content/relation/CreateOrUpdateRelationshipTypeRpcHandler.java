package com.enonic.wem.web.rest.rpc.content.relation;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relation.CreateRelationshipType;
import com.enonic.wem.api.command.content.relation.GetRelationshipTypes;
import com.enonic.wem.api.command.content.relation.UpdateRelationshipTypes;
import com.enonic.wem.api.command.content.relation.editor.RelationshipTypeEditor;
import com.enonic.wem.api.command.content.relation.editor.SetRelationshipTypeEditor;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.core.content.XmlParsingException;
import com.enonic.wem.core.content.relation.RelationshipTypeJsonSerializer;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.relationshipType;

@Component
public final class CreateOrUpdateRelationshipTypeRpcHandler
    extends AbstractDataRpcHandler
{
    private RelationshipTypeJsonSerializer relationshipTypeJsonSerializer;

    public CreateOrUpdateRelationshipTypeRpcHandler()
    {
        super( "relationshipType_createOrUpdate" );
        this.relationshipTypeJsonSerializer = new RelationshipTypeJsonSerializer();
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String json = context.param( "relationshipType" ).required().asObject().toString();

        final RelationshipType relationshipType;

        try
        {
            relationshipType = relationshipTypeJsonSerializer.toRelationshipType( json );
        }
        catch ( XmlParsingException e )
        {
            context.setResult( new JsonErrorResult( "Invalid content type format" ) );
            return;
        }

        if ( !relationshipTypeExists( relationshipType.getQualifiedRelationshipTypeName() ) )
        {
            final CreateRelationshipType createRelationshipType = Commands.relationshipType().create();
            createRelationshipType.relationshipType( relationshipType );
            client.execute( createRelationshipType );
            context.setResult( CreateOrUpdateRelationshipTypeJsonResult.created() );
        }
        else
        {
            final UpdateRelationshipTypes updateRelationshipTypes = Commands.relationshipType().update();
            final RelationshipTypeEditor relationshipTypeEditor = new SetRelationshipTypeEditor( relationshipType );
            updateRelationshipTypes.editor( relationshipTypeEditor );
            client.execute( updateRelationshipTypes );
            context.setResult( CreateOrUpdateRelationshipTypeJsonResult.updated() );
        }
    }

    private boolean relationshipTypeExists( final QualifiedRelationshipTypeName qualifiedRelationshipTypeName )
    {
        final QualifiedRelationshipTypeNames qualifiedRelationshipTypeNames =
            QualifiedRelationshipTypeNames.from( qualifiedRelationshipTypeName );

        final GetRelationshipTypes relationshipTypes = relationshipType().get().names( qualifiedRelationshipTypeNames );

        return !client.execute( relationshipTypes ).isEmpty();
    }

}
