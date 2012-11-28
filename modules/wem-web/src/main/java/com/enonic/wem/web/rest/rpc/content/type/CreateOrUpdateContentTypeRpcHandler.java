package com.enonic.wem.web.rest.rpc.content.type;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.CreateContentType;
import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.command.content.type.UpdateContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.core.content.type.ContentTypeXmlSerializer;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.content.type.editor.ContentTypeEditors.setContentType;

@Component
public class CreateOrUpdateContentTypeRpcHandler
    extends AbstractDataRpcHandler
{
    private final ContentTypeXmlSerializer contentTypeXmlSerializer;

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
        final ContentType contentType = contentTypeXmlSerializer.toContentType( contentTypeXml );

        if ( !contentTypeExists( contentType.getQualifiedName() ) )
        {
            final CreateContentType createContentType = contentType().create().contentType( contentType );
            client.execute( createContentType );
            context.setResult( CreateOrUpdateContentTypeJsonResult.created() );
        }
        else
        {
            final UpdateContentTypes updateContentType = contentType().update().editor( setContentType( contentType ) );
            client.execute( updateContentType );
            context.setResult( CreateOrUpdateContentTypeJsonResult.updated() );
        }
    }

    private boolean contentTypeExists( final QualifiedContentTypeName qualifiedName )
    {
        final GetContentTypes getContentTypes = contentType().get().names( QualifiedContentTypeNames.from( qualifiedName ) );
        return !client.execute( getContentTypes ).isEmpty();
    }
}
