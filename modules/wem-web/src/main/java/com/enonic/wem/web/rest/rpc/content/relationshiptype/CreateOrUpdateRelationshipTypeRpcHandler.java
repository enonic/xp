package com.enonic.wem.web.rest.rpc.content.relationshiptype;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relationshiptype.CreateRelationshipType;
import com.enonic.wem.api.command.content.relationshiptype.RelationshipTypesExists;
import com.enonic.wem.api.command.content.relationshiptype.RelationshipTypesExistsResult;
import com.enonic.wem.api.command.content.relationshiptype.UpdateRelationshipTypes;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.core.content.relationshiptype.RelationshipTypeXmlSerializer;
import com.enonic.wem.core.support.serializer.XmlParsingException;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.service.upload.UploadItem;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static com.enonic.wem.api.command.Commands.relationshipType;
import static com.enonic.wem.api.content.relationshiptype.editor.RelationshipTypeEditors.setRelationshipType;

@Component
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

        final Icon icon = getIconUploaded( iconReference );
        if ( !exists( relationshipType.getQualifiedName() ) )
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
            client.execute( createCommand );
            context.setResult( CreateOrUpdateRelationshipTypeJsonResult.created() );
        }
        else
        {
            final QualifiedRelationshipTypeNames qualifiedNames =
                QualifiedRelationshipTypeNames.from( relationshipType.getQualifiedName() );

            final UpdateRelationshipTypes updateCommand = Commands.relationshipType().update();
            updateCommand.selectors( qualifiedNames );
            updateCommand.editor( setRelationshipType( relationshipType.getDisplayName(), relationshipType.getFromSemantic(),
                                                       relationshipType.getToSemantic(), relationshipType.getAllowedFromTypes(),
                                                       relationshipType.getAllowedToTypes(), icon ) );

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

    private Icon getIconUploaded( final String iconReference )
        throws IOException
    {
        if ( iconReference == null )
        {
            return null;
        }
        final UploadItem uploadItem = uploadService.getItem( iconReference );
        if ( uploadItem != null )
        {
            final byte[] iconData = getUploadedImageData( uploadItem );
            return uploadItem != null ? Icon.from( iconData, uploadItem.getMimeType() ) : null;
        }
        return null;
    }

    private byte[] getUploadedImageData( final UploadItem uploadItem )
        throws IOException
    {
        if ( uploadItem != null )
        {
            final File file = uploadItem.getFile();
            if ( file.exists() )
            {
                return FileUtils.readFileToByteArray( file );
            }
        }
        return null;
    }

    @Autowired
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
