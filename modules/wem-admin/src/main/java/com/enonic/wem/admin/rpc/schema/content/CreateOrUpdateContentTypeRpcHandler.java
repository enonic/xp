package com.enonic.wem.admin.rpc.schema.content;

import javax.inject.Inject;

import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.jsonrpc.JsonRpcException;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.admin.rpc.UploadedIconFetcher;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.schema.content.UpdateContentType;
import com.enonic.wem.api.command.schema.content.UpdateContentTypeResult;
import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;
import com.enonic.wem.core.support.serializer.XmlParsingException;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.editor.SetContentTypeEditor.newSetContentTypeEditor;


public class CreateOrUpdateContentTypeRpcHandler
    extends AbstractDataRpcHandler
{

    private final ContentTypeXmlSerializer contentTypeXmlSerializer;

    private UploadService uploadService;

    public CreateOrUpdateContentTypeRpcHandler()
    {
        super( "contentType_createOrUpdate" );
        this.contentTypeXmlSerializer = new ContentTypeXmlSerializer();
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String name = context.param( "name" ).required().asString();
        final String contentTypeXml = context.param( "contentType" ).required().asString();
        final String iconReference = context.param( "iconReference" ).asString();
        ContentType contentType;
        try
        {
            contentType = contentTypeXmlSerializer.toContentType( contentTypeXml );
        }
        catch ( XmlParsingException e )
        {
            context.setResult( new JsonErrorResult( "Invalid content type format: " + e.getMessage() ) );
            return;
        }

        contentType = newContentType( contentType ).name( name ).build();

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

        if ( icon != null )
        {
            contentType = newContentType( contentType ).icon( icon ).build();
        }

        if ( !contentTypeExists( contentType.getQualifiedName() ) )
        {
            createContentType( context, contentType );
        }
        else
        {
            updateContentType( context, contentType );
        }
    }

    private void updateContentType( final JsonRpcContext context, final ContentType contentType )
    {
        final ContentTypeEditor editor = newSetContentTypeEditor().
            displayName( contentType.getDisplayName() ).
            icon( contentType.getIcon() ).
            superType( contentType.getSuperType() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() ).
            form( contentType.form() ).
            build();
        final UpdateContentType updateCommand = contentType().update().qualifiedName( contentType.getQualifiedName() ).editor( editor );

        try
        {
            UpdateContentTypeResult result = client.execute( updateCommand );
            context.setResult( CreateOrUpdateContentTypeJsonResult.from( result ) );
        }
        catch ( InvalidContentTypeException e )
        {
            context.setResult( new JsonErrorResult( e.getValidationMessage() ) );
        }
        catch ( BaseException e )
        {
            context.setResult( new JsonErrorResult( e.getMessage() ) );
        }
    }

    private void createContentType( final JsonRpcContext context, final ContentType contentType )
    {
        final CreateContentType createCommand = contentType().create().
            name( contentType.getName() ).
            displayName( contentType.getDisplayName() ).
            superType( contentType.getSuperType() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            form( contentType.form() ).
            icon( contentType.getIcon() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() );
        try
        {
            client.execute( createCommand );
            context.setResult( CreateOrUpdateContentTypeJsonResult.created() );
        }
        catch ( InvalidContentTypeException e )
        {
            context.setResult( new JsonErrorResult( e.getValidationMessage() ) );
        }
        catch ( BaseException e )
        {
            context.setResult( new JsonErrorResult( e.getMessage() ) );
        }
    }

    private boolean contentTypeExists( final ContentTypeName qualifiedName )
    {
        final GetContentTypes getContentTypes = contentType().get().qualifiedNames( ContentTypeNames.from( qualifiedName ) );
        return !client.execute( getContentTypes ).isEmpty();
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
