package com.enonic.wem.web.rest.rpc.content.type;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.contentType;

@Component
public class GetContentTypeConfigRpcHandler
    extends AbstractDataRpcHandler
{
    public GetContentTypeConfigRpcHandler()
    {
        super( "contentType_getConfig" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String qualifiedContentTypeName = context.param( "qualifiedContentTypeName" ).required().asString();
        final GetContentTypes getContentTypes = contentType().get().names( QualifiedContentTypeNames.from( qualifiedContentTypeName ) );
        final ContentType contentType = client.execute( getContentTypes ).first();
        if ( contentType == null )
        {
            context.setResult( new JsonErrorResult( "Content type [{0}] was not found", qualifiedContentTypeName ) );
        }
        else
        {
            context.setResult( new GetContentTypeConfigRpcJsonResult( contentType ) );
        }
    }

}
