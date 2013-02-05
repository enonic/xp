package com.enonic.wem.web.rest.rpc.content.relationship;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relationship.CreateRelationshipType;
import com.enonic.wem.api.command.content.relationship.RelationshipTypesExists;
import com.enonic.wem.api.command.content.relationship.RelationshipTypesExistsResult;
import com.enonic.wem.api.command.content.relationship.UpdateRelationshipTypes;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationship.RelationshipType;
import com.enonic.wem.core.content.XmlParsingException;
import com.enonic.wem.core.content.relationship.RelationshipTypeJsonSerializer;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.relationshipType;
import static com.enonic.wem.api.command.content.relationship.editor.RelationshipTypeEditors.setRelationshipType;

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
            relationshipType = relationshipTypeJsonSerializer.toObject( json );
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
            updateCommand.selectors( qualifiedNames );
            updateCommand.editor( setRelationshipType( relationshipType ) );

            client.execute( updateCommand );

            context.setResult( CreateOrUpdateRelationshipTypeJsonResult.updated() );
        }
    }

    private boolean exists( final QualifiedRelationshipTypeName qualifiedName )
    {
        final RelationshipTypesExists existsCommand =
            relationshipType().exists().selectors( QualifiedRelationshipTypeNames.from( qualifiedName ) );
        final RelationshipTypesExistsResult existsResult = client.execute( existsCommand );
        return existsResult.exists( qualifiedName );
    }

}
