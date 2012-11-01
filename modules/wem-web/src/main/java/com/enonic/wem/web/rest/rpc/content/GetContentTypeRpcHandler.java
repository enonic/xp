package com.enonic.wem.web.rest.rpc.content;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class GetContentTypeRpcHandler
    extends AbstractDataRpcHandler
{

    public GetContentTypeRpcHandler()
    {
        super( "contentType_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final QualifiedContentTypeName contentTypeName =
            new QualifiedContentTypeName( context.param( "contentType" ).required().asString() );

        final ContentType contentType = fetchContentType( contentTypeName );

        if ( contentType != null )
        {
            context.setResult( new GetContentTypeRpcJsonResult( contentType ) );
        }
        else
        {
            context.setResult( new JsonErrorResult( "Content type [{0}] was not found", contentTypeName ) );
        }
    }

    private ContentType fetchContentType( final QualifiedContentTypeName contentTypeName )
    {
        final ContentTypes contentTypeResult = client.execute( Commands.contentType().get().names( contentTypeName ) );
        return contentTypeResult.isEmpty() ? null : contentTypeResult.getFirst();
    }
}
