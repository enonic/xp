package com.enonic.wem.web.rest.rpc.content.relation;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relation.CreateRelationshipType;
import com.enonic.wem.api.command.content.relation.RelationshipTypesExists;
import com.enonic.wem.api.command.content.relation.RelationshipTypesExistsResult;
import com.enonic.wem.api.command.content.relation.UpdateRelationshipTypes;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.core.content.XmlParsingException;
import com.enonic.wem.core.content.relationship.RelationshipTypeJsonSerializer;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.relationshipType;
import static com.enonic.wem.api.command.content.relation.editor.RelationshipTypeEditors.setRelationshipType;

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
            context.setResult( new JsonErrorResult( "Invalid RelationshipType format" ) );
            return;
        }

        if ( !exists( relationshipType.getQualifiedName() ) )
        {
            final CreateRelationshipType createCommand = Commands.relationshipType().create();
            createCommand.relationshipType( relationshipType );
            client.execute( createCommand );
            context.setResult( CreateOrUpdateRelationshipTypeJsonResult.created() );
        }
        else
        {
            final QualifiedRelationshipTypeNames qualifiedNames =
                QualifiedRelationshipTypeNames.from( relationshipType.getQualifiedName() );

            final UpdateRelationshipTypes updateCommand = Commands.relationshipType().update();
            updateCommand.qualifiedNames( qualifiedNames );
            updateCommand.editor( setRelationshipType( relationshipType ) );

            client.execute( updateCommand );

            context.setResult( CreateOrUpdateRelationshipTypeJsonResult.updated() );
        }
    }

    private boolean exists( final QualifiedRelationshipTypeName qualifiedName )
    {
        final RelationshipTypesExists existsCommand =
            relationshipType().exists().qualifiedNames( QualifiedRelationshipTypeNames.from( qualifiedName ) );
        final RelationshipTypesExistsResult existsResult = client.execute( existsCommand );
        return existsResult.exists( qualifiedName );
    }

}
