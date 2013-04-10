package com.enonic.wem.web.rest.rpc.content.schema.content;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.content.schema.content.CreateContentType;
import com.enonic.wem.api.command.content.schema.content.GetContentTypes;
import com.enonic.wem.api.command.content.schema.content.UpdateContentType;
import com.enonic.wem.api.command.content.schema.content.UpdateContentTypeResult;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.content.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.core.content.schema.content.serializer.ContentTypeXmlSerializer;
import com.enonic.wem.core.support.serializer.XmlParsingException;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.json.rpc.JsonRpcException;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.rpc.UploadedIconFetcher;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.editor.SetContentTypeEditor.newSetContentTypeEditor;

@Component
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
        final String contentTypeXml = context.param( "contentType" ).required().asString();
        final String iconReference = context.param( "iconReference" ).asString();
        ContentType contentType;
        try
        {
            contentType = contentTypeXmlSerializer.toContentType( contentTypeXml );
        }
        catch ( XmlParsingException e )
        {
            context.setResult( new JsonErrorResult( "Invalid content type format" ) );
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
            moduleName( contentType.getModuleName() ).
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

    private boolean contentTypeExists( final QualifiedContentTypeName qualifiedName )
    {
        final GetContentTypes getContentTypes = contentType().get().qualifiedNames( QualifiedContentTypeNames.from( qualifiedName ) );
        return !client.execute( getContentTypes ).isEmpty();
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
