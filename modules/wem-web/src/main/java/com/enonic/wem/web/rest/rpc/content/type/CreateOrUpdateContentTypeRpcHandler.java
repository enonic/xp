package com.enonic.wem.web.rest.rpc.content.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.content.type.CreateContentType;
import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.command.content.type.UpdateContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.core.content.type.ContentTypeXmlSerializer;
import com.enonic.wem.core.support.serializer.XmlParsingException;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.json.rpc.JsonRpcException;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.rpc.UploadedIconFetcher;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.editor.ContentTypeEditors.setContentType;

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
            final CreateContentType createCommand = contentType().create().contentType( contentType );
            client.execute( createCommand );

            context.setResult( CreateOrUpdateContentTypeJsonResult.created() );
        }
        else
        {
            final QualifiedContentTypeNames qualifiedContentTypeNames = QualifiedContentTypeNames.from( contentType.getQualifiedName() );

            final UpdateContentTypes updateCommand =
                contentType().update().names( qualifiedContentTypeNames ).editor( setContentType( contentType ) );

            client.execute( updateCommand );

            context.setResult( CreateOrUpdateContentTypeJsonResult.updated() );
        }
    }

    private boolean contentTypeExists( final QualifiedContentTypeName qualifiedName )
    {
        final GetContentTypes getContentTypes = contentType().get().names( QualifiedContentTypeNames.from( qualifiedName ) );
        return !client.execute( getContentTypes ).isEmpty();
    }

    @Autowired
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
