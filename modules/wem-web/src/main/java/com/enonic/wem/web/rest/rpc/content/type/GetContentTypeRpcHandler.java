package com.enonic.wem.web.rest.rpc.content.type;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class GetContentTypeRpcHandler
    extends AbstractDataRpcHandler
{

    public static final String FORMAT_XML = "XML";

    public static final String FORMAT_JSON = "JSON";

    public GetContentTypeRpcHandler()
    {
        super( "contentType_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String format = context.param( "format" ).required().asString();
        final QualifiedContentTypeName qualifiedName = new QualifiedContentTypeName( context.param( "contentType" ).required().asString() );
        final GetContentTypes getContentTypes = Commands.contentType().get().names( QualifiedContentTypeNames.from( qualifiedName ) );
        getContentTypes.mixinReferencesToFormItems( context.param( "mixinReferencesToFormItems" ).asBoolean( false ) );
        final ContentTypes result = client.execute( getContentTypes );

        if ( !result.isEmpty() )
        {
            if ( format.equalsIgnoreCase( FORMAT_JSON ) )
            {
                context.setResult( new GetContentTypeRpcJsonResult( result.first() ) );
            }
            else if ( format.equalsIgnoreCase( FORMAT_XML ) )
            {
                context.setResult( new GetContentTypeConfigRpcJsonResult( result.first() ) );
            }
        }
        else
        {
            context.setResult( new JsonErrorResult( "Content type [{0}] was not found", qualifiedName ) );
        }
    }
}
