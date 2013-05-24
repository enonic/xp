package com.enonic.wem.admin.rest.rpc.schema.relationship;


import javax.inject.Inject;

import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.json.rpc.JsonRpcException;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.admin.rest.rpc.UploadedIconFetcher;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.CreateRelationshipType;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypesExists;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypesExistsResult;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.editor.SetRelationshipTypeEditor;
import com.enonic.wem.core.schema.relationship.RelationshipTypeXmlSerializer;
import com.enonic.wem.core.support.serializer.XmlParsingException;

import static com.enonic.wem.api.command.Commands.relationshipType;


public final class CreateOrUpdateRelationshipTypeRpcHandler
    extends AbstractDataRpcHandler
{
    private RelationshipTypeXmlSerializer relationshipTypeXmlSerializer;

    private UploadService uploadService;

    public CreateOrUpdateRelationshipTypeRpcHandler()
    {
        super( "relationshipType_createOrUpdate" );
        this.relationshipTypeXmlSerializer = new RelationshipTypeXmlSerializer();
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String xml = context.param( "relationshipType" ).required().asString();
        final String iconReference = context.param( "iconReference" ).asString();
        final RelationshipType relationshipType;
        try
        {
            relationshipType = relationshipTypeXmlSerializer.toRelationshipType( xml );
        }
        catch ( XmlParsingException e )
        {
            context.setResult( new JsonErrorResult( "Invalid RelationshipType format" ) );
            return;
        }

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( iconReference );
        }
        catch ( JsonRpcException e )
        {
            context.setResult( new JsonErrorResult( e.getError().getMessage() ) );
            return;
        }

        if ( !exists( relationshipType.getQualifiedName() ) )
        {
            createRelationshipType( context, relationshipType, icon );
        }
        else
        {
            updateRelationshipType( context, relationshipType, icon );
        }
    }

    private void updateRelationshipType( final JsonRpcContext context, final RelationshipType relationshipType, final Icon icon )
    {
        final UpdateRelationshipType updateCommand = Commands.relationshipType().update();
        updateCommand.selector( relationshipType.getQualifiedName() );
        updateCommand.editor( SetRelationshipTypeEditor.newSetRelationshipTypeEditor().
            displayName( relationshipType.getDisplayName() ).
            fromSemantic( relationshipType.getFromSemantic() ).
            toSemantic( relationshipType.getToSemantic() ).
            allowedFromTypes( relationshipType.getAllowedFromTypes() ).
            allowedToTypes( relationshipType.getAllowedToTypes() ).
            icon( icon ).
            build() );

        try
        {
            client.execute( updateCommand );
            context.setResult( CreateOrUpdateRelationshipTypeJsonResult.updated() );
        }
        catch ( BaseException e )
        {
            context.setResult( new JsonErrorResult( e.getMessage() ) );
        }
    }

    private void createRelationshipType( final JsonRpcContext context, final RelationshipType relationshipType, final Icon icon )
    {
        final CreateRelationshipType createCommand = Commands.relationshipType().create();
        createCommand.
            module( relationshipType.getModuleName() ).
            name( relationshipType.getName() ).
            displayName( relationshipType.getDisplayName() ).
            fromSemantic( relationshipType.getFromSemantic() ).
            toSemantic( relationshipType.getToSemantic() ).
            allowedFromTypes( relationshipType.getAllowedFromTypes() ).
            allowedToTypes( relationshipType.getAllowedToTypes() ).
            icon( icon );

        try
        {
            client.execute( createCommand );
            context.setResult( CreateOrUpdateRelationshipTypeJsonResult.created() );
        }
        catch ( BaseException e )
        {
            context.setResult( new JsonErrorResult( e.getMessage() ) );
        }
    }

    private boolean exists( final QualifiedRelationshipTypeName qualifiedName )
    {
        final RelationshipTypesExists existsCommand =
            relationshipType().exists().qualifiedNames( QualifiedRelationshipTypeNames.from( qualifiedName ) );
        final RelationshipTypesExistsResult existsResult = client.execute( existsCommand );
        return existsResult.exists( qualifiedName );
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
